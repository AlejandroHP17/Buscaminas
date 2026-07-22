package pelkidev.com.mx.buscaminas.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pelkidev.com.mx.buscaminas.data.GameSaveRepositoryImpl
import pelkidev.com.mx.buscaminas.domain.game.MinesweeperEngine
import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.GameStatus
import pelkidev.com.mx.buscaminas.domain.model.SavedGameInfo
import pelkidev.com.mx.buscaminas.domain.model.SavedGameState
import pelkidev.com.mx.buscaminas.domain.repository.GameSaveRepository

class MinesweeperViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val gameSaveRepository: GameSaveRepository = GameSaveRepositoryImpl(application)

    private val _uiState = MutableStateFlow(MinesweeperUiState())
    val uiState: StateFlow<MinesweeperUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            refreshSavedGameAvailability()
        }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun updateCustomCols(cols: Int) {
        _uiState.update { state ->
            val clampedCols = cols.coerceIn(Difficulty.CUSTOM_MIN_SIZE, Difficulty.CUSTOM_MAX_SIZE)
            val maxMines = (state.customRows * clampedCols - 1).coerceAtLeast(Difficulty.CUSTOM_MIN_MINES)
            state.copy(
                customCols = clampedCols,
                customMineCount = state.customMineCount.coerceIn(Difficulty.CUSTOM_MIN_MINES, maxMines),
            )
        }
    }

    fun updateCustomRows(rows: Int) {
        _uiState.update { state ->
            val clampedRows = rows.coerceIn(Difficulty.CUSTOM_MIN_SIZE, Difficulty.CUSTOM_MAX_SIZE)
            val maxMines = (clampedRows * state.customCols - 1).coerceAtLeast(Difficulty.CUSTOM_MIN_MINES)
            state.copy(
                customRows = clampedRows,
                customMineCount = state.customMineCount.coerceIn(Difficulty.CUSTOM_MIN_MINES, maxMines),
            )
        }
    }

    fun updateCustomMineCount(mines: Int) {
        _uiState.update { state ->
            val maxMines = (state.customRows * state.customCols - 1).coerceAtLeast(Difficulty.CUSTOM_MIN_MINES)
            state.copy(
                customMineCount = mines.coerceIn(Difficulty.CUSTOM_MIN_MINES, maxMines),
            )
        }
    }

    fun startGame() {
        viewModelScope.launch {
            gameSaveRepository.clearSavedGame()
            stopTimer()
            _uiState.update { state ->
                val boardConfig = resolveBoardConfig(state)
                state.copy(
                    screen = AppScreen.GAME,
                    customRows = boardConfig.rows,
                    customCols = boardConfig.cols,
                    customMineCount = boardConfig.mineCount,
                    board = Board.empty(
                        rows = boardConfig.rows,
                        cols = boardConfig.cols,
                        mineCount = boardConfig.mineCount,
                    ),
                    gameStatus = GameStatus.NOT_STARTED,
                    elapsedSeconds = 0,
                    hasSavedGame = false,
                    savedGameInfo = null,
                )
            }
            persistCurrentGame()
        }
    }

    fun continueGame() {
        viewModelScope.launch {
            val saved = gameSaveRepository.loadGame() ?: return@launch
            stopTimer()
            _uiState.value = MinesweeperUiState(
                screen = AppScreen.GAME,
                selectedDifficulty = saved.difficulty,
                customRows = saved.board.rows,
                customCols = saved.board.cols,
                customMineCount = saved.board.mineCount,
                board = saved.board,
                gameStatus = saved.gameStatus,
                elapsedSeconds = saved.elapsedSeconds,
            )
            if (saved.gameStatus == GameStatus.PLAYING) {
                startTimer()
            }
        }
    }

    fun restartGame() {
        startGame()
    }

    fun goToStartScreen() {
        stopTimer()
        viewModelScope.launch {
            persistCurrentGame()
            refreshSavedGameAvailability()
            _uiState.update { it.copy(screen = AppScreen.START) }
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val state = _uiState.value
        if (state.gameStatus == GameStatus.WON || state.gameStatus == GameStatus.LOST) return

        var board = state.board
        var gameStatus = state.gameStatus

        if (!board.isGenerated) {
            board = MinesweeperEngine.generateMines(board, row, col)
            gameStatus = GameStatus.PLAYING
            startTimer()
        }

        val result = MinesweeperEngine.revealCell(board, row, col)
        board = result.board

        when {
            result.hitMine -> {
                gameStatus = GameStatus.LOST
                stopTimer()
            }
            result.won -> {
                gameStatus = GameStatus.WON
                stopTimer()
            }
            gameStatus == GameStatus.NOT_STARTED -> {
                gameStatus = GameStatus.PLAYING
            }
        }

        _uiState.update {
            it.copy(board = board, gameStatus = gameStatus)
        }
        onGameStateChanged()
    }

    fun onCellLongClick(row: Int, col: Int): Boolean {
        val state = _uiState.value
        if (state.gameStatus == GameStatus.WON || state.gameStatus == GameStatus.LOST) {
            return false
        }

        val cell = state.board.cellAt(row, col)
        if (cell.isRevealed) return false

        if (state.gameStatus == GameStatus.NOT_STARTED) {
            _uiState.update { it.copy(gameStatus = GameStatus.PLAYING) }
            startTimer()
        }

        val updatedBoard = MinesweeperEngine.toggleFlag(state.board, row, col)
        _uiState.update { it.copy(board = updatedBoard) }
        onGameStateChanged()
        return true
    }

    private fun onGameStateChanged() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.gameStatus == GameStatus.WON || state.gameStatus == GameStatus.LOST) {
                gameSaveRepository.clearSavedGame()
                _uiState.update { it.copy(hasSavedGame = false, savedGameInfo = null) }
            } else {
                persistCurrentGame()
            }
        }
    }

    private suspend fun persistCurrentGame() {
        val state = _uiState.value
        if (state.screen != AppScreen.GAME || !isGameResumable(state)) return

        gameSaveRepository.saveGame(
            SavedGameState(
                difficulty = state.selectedDifficulty,
                board = state.board,
                gameStatus = state.gameStatus,
                elapsedSeconds = state.elapsedSeconds,
            ),
        )
    }

    private suspend fun refreshSavedGameAvailability() {
        val saved = gameSaveRepository.loadGame()
        _uiState.update {
            it.copy(
                hasSavedGame = saved != null,
                savedGameInfo = saved?.let { game ->
                    SavedGameInfo(
                        difficulty = game.difficulty,
                        elapsedSeconds = game.elapsedSeconds,
                    )
                },
            )
        }
    }

    private fun isGameResumable(state: MinesweeperUiState): Boolean {
        return state.gameStatus == GameStatus.NOT_STARTED || state.gameStatus == GameStatus.PLAYING
    }

    private fun resolveBoardConfig(state: MinesweeperUiState): BoardConfig {
        val difficulty = state.selectedDifficulty
        return if (difficulty == Difficulty.PERSONALIZED) {
            val rows = state.customRows.coerceIn(Difficulty.CUSTOM_MIN_SIZE, Difficulty.CUSTOM_MAX_SIZE)
            val cols = state.customCols.coerceIn(Difficulty.CUSTOM_MIN_SIZE, Difficulty.CUSTOM_MAX_SIZE)
            val maxMines = (rows * cols - 1).coerceAtLeast(Difficulty.CUSTOM_MIN_MINES)
            BoardConfig(
                rows = rows,
                cols = cols,
                mineCount = state.customMineCount.coerceIn(Difficulty.CUSTOM_MIN_MINES, maxMines),
            )
        } else {
            BoardConfig(
                rows = difficulty.rows,
                cols = difficulty.cols,
                mineCount = difficulty.mineCount,
            )
        }
    }

    private data class BoardConfig(
        val rows: Int,
        val cols: Int,
        val mineCount: Int,
    )

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(TIMER_INTERVAL_MS)
                val status = _uiState.value.gameStatus
                if (status != GameStatus.PLAYING) break
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        stopTimer()
        runBlocking {
            persistCurrentGame()
        }
        super.onCleared()
    }

    companion object {
        private const val TIMER_INTERVAL_MS = 1_000L
    }
}
