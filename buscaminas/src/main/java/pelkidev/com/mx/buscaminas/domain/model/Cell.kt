package pelkidev.com.mx.buscaminas.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Cell(
    val row: Int,
    val col: Int,
    val isMine: Boolean = false,
    val isRevealed: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMines: Int = 0,
    val isExploded: Boolean = false,
)
