package pelkidev.com.mx.buscaminas.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pelkidev.com.mx.buscaminas.R
import pelkidev.com.mx.buscaminas.domain.model.GameStatus
import pelkidev.com.mx.buscaminas.presentation.components.GameTopBar
import pelkidev.com.mx.buscaminas.presentation.components.GridComponent
import pelkidev.com.mx.buscaminas.presentation.components.StartScreen

@Composable
fun MinesweeperScreen(
    onExit: (() -> Unit)? = null,
    viewModel: MinesweeperViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.screen) {
        AppScreen.START -> {
            StartScreen(
                selectedDifficulty = uiState.selectedDifficulty,
                customCols = uiState.customCols,
                customRows = uiState.customRows,
                customMineCount = uiState.customMineCount,
                hasSavedGame = uiState.hasSavedGame,
                savedGameInfo = uiState.savedGameInfo,
                onDifficultySelected = viewModel::selectDifficulty,
                onCustomColsChanged = viewModel::updateCustomCols,
                onCustomRowsChanged = viewModel::updateCustomRows,
                onCustomMineCountChanged = viewModel::updateCustomMineCount,
                onStartGame = viewModel::startGame,
                onContinueGame = viewModel::continueGame,
                onExit = onExit,
            )
        }

        AppScreen.GAME -> {
            GameScreen(
                uiState = uiState,
                onRestart = viewModel::restartGame,
                onBack = viewModel::goToStartScreen,
                onCellClick = viewModel::onCellClick,
                onCellLongClick = viewModel::onCellLongClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreen(
    uiState: MinesweeperUiState,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    onCellClick: (Int, Int) -> Unit,
    onCellLongClick: (Int, Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.selectedDifficulty.displayName,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_revert),
                            contentDescription = stringResource(R.string.buscaminas_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            GameTopBar(
                minesRemaining = uiState.minesRemaining,
                elapsedSeconds = uiState.elapsedSeconds,
                gameStatus = uiState.gameStatus,
                onRestart = onRestart,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                GridComponent(
                    board = uiState.board,
                    difficulty = uiState.selectedDifficulty,
                    gameStatus = uiState.gameStatus,
                    onCellClick = onCellClick,
                    onCellLongClick = onCellLongClick,
                )
            }

            if (uiState.gameStatus == GameStatus.WON || uiState.gameStatus == GameStatus.LOST) {
                Text(
                    text = when (uiState.gameStatus) {
                        GameStatus.WON -> stringResource(R.string.buscaminas_game_won)
                        GameStatus.LOST -> stringResource(R.string.buscaminas_game_lost)
                        else -> ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (uiState.gameStatus) {
                        GameStatus.WON -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    },
                )
            }
        }
    }
}
