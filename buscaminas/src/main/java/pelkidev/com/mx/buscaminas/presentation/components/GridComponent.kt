package pelkidev.com.mx.buscaminas.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.GameStatus

@Composable
fun GridComponent(
    board: Board,
    difficulty: Difficulty,
    gameStatus: GameStatus,
    onCellClick: (row: Int, col: Int) -> Unit,
    onCellLongClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellSize = remember(difficulty) { cellSizeForDifficulty(difficulty) }
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(verticalScroll)
            .horizontalScroll(horizontalScroll)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        for (rowIndex in 0 until board.rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                val rowCells = board.cells[rowIndex]
                for (colIndex in 0 until board.cols) {
                    val cell = rowCells[colIndex]
                    androidx.compose.runtime.key(cell.row, cell.col) {
                        CellComponent(
                            cell = cell,
                            gameStatus = gameStatus,
                            cellSize = cellSize,
                            onClick = { onCellClick(cell.row, cell.col) },
                            onLongClick = { onCellLongClick(cell.row, cell.col) },
                        )
                    }
                }
            }
        }
    }
}

private fun cellSizeForDifficulty(difficulty: Difficulty): Dp = when (difficulty) {
    Difficulty.BEGINNER -> 36.dp
    Difficulty.INTERMEDIATE -> 34.dp
    Difficulty.EXPERT -> 32.dp
    Difficulty.PERSONALIZED -> 30.dp
}
