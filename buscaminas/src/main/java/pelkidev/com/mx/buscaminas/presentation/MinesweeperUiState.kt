package pelkidev.com.mx.buscaminas.presentation

import androidx.compose.runtime.Immutable
import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.GameStatus
import pelkidev.com.mx.buscaminas.domain.model.SavedGameInfo

enum class AppScreen {
    START,
    GAME,
}

@Immutable
data class MinesweeperUiState(
    val screen: AppScreen = AppScreen.START,
    val selectedDifficulty: Difficulty = Difficulty.BEGINNER,
    val customRows: Int = Difficulty.PERSONALIZED.rows,
    val customCols: Int = Difficulty.PERSONALIZED.cols,
    val customMineCount: Int = Difficulty.PERSONALIZED.mineCount,
    val board: Board = Board.empty(
        rows = Difficulty.BEGINNER.rows,
        cols = Difficulty.BEGINNER.cols,
        mineCount = Difficulty.BEGINNER.mineCount,
    ),
    val gameStatus: GameStatus = GameStatus.NOT_STARTED,
    val elapsedSeconds: Int = 0,
    val hasSavedGame: Boolean = false,
    val savedGameInfo: SavedGameInfo? = null,
) {
    val minesRemaining: Int
        get() = (board.mineCount - board.flagCount).coerceAtLeast(0)
}
