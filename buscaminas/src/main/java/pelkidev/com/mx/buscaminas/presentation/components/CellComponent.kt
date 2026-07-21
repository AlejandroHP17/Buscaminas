package pelkidev.com.mx.buscaminas.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pelkidev.com.mx.buscaminas.domain.model.Cell
import pelkidev.com.mx.buscaminas.domain.model.GameStatus
import pelkidev.com.mx.buscaminas.ui.theme.MineCellExploded
import pelkidev.com.mx.buscaminas.ui.theme.MineCellFlagged
import pelkidev.com.mx.buscaminas.ui.theme.MineCellHidden
import pelkidev.com.mx.buscaminas.ui.theme.MineCellHiddenBorderDark
import pelkidev.com.mx.buscaminas.ui.theme.MineCellHiddenBorderLight
import pelkidev.com.mx.buscaminas.ui.theme.MineCellRevealed
import pelkidev.com.mx.buscaminas.ui.theme.MineCellRevealedBorder
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber1
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber2
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber3
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber4
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber5
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber6
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber7
import pelkidev.com.mx.buscaminas.ui.theme.MineNumber8

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellComponent(
    cell: Cell,
    gameStatus: GameStatus,
    cellSize: Dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val isGameOver = gameStatus == GameStatus.WON || gameStatus == GameStatus.LOST

    Box(
        modifier = modifier
            .size(cellSize)
            .clip(RoundedCornerShape(2.dp))
            .then(cellBackgroundModifier(cell, gameStatus))
            .then(
                if (!isGameOver && !cell.isRevealed) {
                    Modifier.combinedClickable(
                        onClick = onClick,
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongClick()
                        },
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        CellContent(cell = cell, gameStatus = gameStatus)
    }
}

@Composable
private fun CellContent(cell: Cell, gameStatus: GameStatus) {
    when {
        cell.isFlagged && !cell.isRevealed -> {
            Text(
                text = "🚩",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
        cell.isRevealed && cell.isMine -> {
            Text(
                text = "💣",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
        cell.isRevealed && cell.adjacentMines > 0 -> {
            Text(
                text = cell.adjacentMines.toString(),
                color = numberColor(cell.adjacentMines),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        gameStatus == GameStatus.WON && cell.isMine && !cell.isRevealed -> {
            Text(
                text = "🚩",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun cellBackgroundModifier(cell: Cell, gameStatus: GameStatus): Modifier {
    return when {
        cell.isRevealed && cell.isExploded -> {
            Modifier
                .background(MineCellExploded)
                .border(1.dp, MineCellRevealedBorder)
        }
        // Bandera correcta sobre una bomba al perder
        cell.isRevealed && cell.isMine && cell.isFlagged -> {
            Modifier
                .background(MineCellFlagged)
                .border(1.dp, MineCellRevealedBorder)
        }
        cell.isRevealed && cell.isMine -> {
            Modifier
                .background(MineCellExploded.copy(alpha = 0.85f))
                .border(1.dp, MineCellRevealedBorder)
        }
        cell.isRevealed -> {
            Modifier
                .background(MineCellRevealed)
                .border(1.dp, MineCellRevealedBorder)
        }
        cell.isFlagged -> {
            Modifier
                .background(MineCellFlagged)
                .border(
                    width = 2.dp,
                    color = MineCellHiddenBorderLight,
                    shape = RoundedCornerShape(2.dp),
                )
                .border(
                    width = 1.dp,
                    color = MineCellHiddenBorderDark,
                    shape = RoundedCornerShape(2.dp),
                )
        }
        else -> {
            Modifier
                .background(MineCellHidden)
                .border(
                    width = 2.dp,
                    color = MineCellHiddenBorderLight,
                    shape = RoundedCornerShape(2.dp),
                )
                .border(
                    width = 1.dp,
                    color = MineCellHiddenBorderDark,
                    shape = RoundedCornerShape(2.dp),
                )
        }
    }
}

private fun numberColor(value: Int): Color = when (value) {
    1 -> MineNumber1
    2 -> MineNumber2
    3 -> MineNumber3
    4 -> MineNumber4
    5 -> MineNumber5
    6 -> MineNumber6
    7 -> MineNumber7
    else -> MineNumber8
}
