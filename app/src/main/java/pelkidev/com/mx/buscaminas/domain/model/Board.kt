package pelkidev.com.mx.buscaminas.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Board(
    val rows: Int,
    val cols: Int,
    val mineCount: Int,
    val cells: List<List<Cell>>,
    val isGenerated: Boolean = false,
) {
    val flagCount: Int
        get() = cells.flatten().count { it.isFlagged }

    val revealedSafeCount: Int
        get() = cells.flatten().count { it.isRevealed && !it.isMine }

    val totalSafeCells: Int
        get() = rows * cols - mineCount

    fun cellAt(row: Int, col: Int): Cell = cells[row][col]

    fun updateCell(row: Int, col: Int, transform: (Cell) -> Cell): Board {
        val newRows = cells.mapIndexed { rowIndex, rowCells ->
            if (rowIndex != row) {
                rowCells
            } else {
                rowCells.mapIndexed { colIndex, cell ->
                    if (colIndex == col) transform(cell) else cell
                }
            }
        }
        return copy(cells = newRows)
    }

    fun replaceCells(newCells: List<List<Cell>>): Board = copy(cells = newCells)

    companion object {
        fun empty(rows: Int, cols: Int, mineCount: Int): Board {
            val cells = List(rows) { row ->
                List(cols) { col ->
                    Cell(row = row, col = col)
                }
            }
            return Board(
                rows = rows,
                cols = cols,
                mineCount = mineCount,
                cells = cells,
                isGenerated = false,
            )
        }
    }
}
