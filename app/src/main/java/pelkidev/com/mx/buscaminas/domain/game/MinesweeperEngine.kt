package pelkidev.com.mx.buscaminas.domain.game

import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Cell

data class RevealResult(
    val board: Board,
    val hitMine: Boolean,
    val won: Boolean,
)

object MinesweeperEngine {

    fun generateMines(board: Board, safeRow: Int, safeCol: Int): Board {
        val safeZone = buildSafeZone(board.rows, board.cols, safeRow, safeCol)
        val availablePositions = mutableListOf<Pair<Int, Int>>()

        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                if ((row to col) !in safeZone) {
                    availablePositions.add(row to col)
                }
            }
        }

        val minePositions = availablePositions
            .shuffled()
            .take(board.mineCount)
            .toSet()

        val newCells = List(board.rows) { row ->
            List(board.cols) { col ->
                val isMine = (row to col) in minePositions
                board.cellAt(row, col).copy(
                    isMine = isMine,
                    adjacentMines = if (isMine) 0 else countAdjacentMines(minePositions, row, col),
                )
            }
        }

        return board.copy(cells = newCells, isGenerated = true)
    }

    fun revealCell(board: Board, row: Int, col: Int): RevealResult {
        val cell = board.cellAt(row, col)
        if (cell.isRevealed || cell.isFlagged) {
            return RevealResult(board = board, hitMine = false, won = false)
        }

        if (cell.isMine) {
            val explodedBoard = revealAllMinesOnLoss(board, row, col)
            return RevealResult(board = explodedBoard, hitMine = true, won = false)
        }

        val revealedBoard = floodReveal(board, row, col)
        val won = revealedBoard.revealedSafeCount == revealedBoard.totalSafeCells
        val finalBoard = if (won) autoFlagMinesOnWin(revealedBoard) else revealedBoard

        return RevealResult(board = finalBoard, hitMine = false, won = won)
    }

    fun toggleFlag(board: Board, row: Int, col: Int): Board {
        val cell = board.cellAt(row, col)
        if (cell.isRevealed) return board

        return board.updateCell(row, col) { it.copy(isFlagged = !it.isFlagged) }
    }

    private fun floodReveal(board: Board, startRow: Int, startCol: Int): Board {
        var currentBoard = board
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(startRow to startCol)

        while (queue.isNotEmpty()) {
            val (row, col) = queue.removeFirst()
            val cell = currentBoard.cellAt(row, col)

            if (cell.isRevealed || cell.isFlagged || cell.isMine) continue

            currentBoard = currentBoard.updateCell(row, col) { it.copy(isRevealed = true) }

            if (currentBoard.cellAt(row, col).adjacentMines == 0) {
                for ((neighborRow, neighborCol) in neighbors(currentBoard, row, col)) {
                    val neighbor = currentBoard.cellAt(neighborRow, neighborCol)
                    if (!neighbor.isRevealed && !neighbor.isFlagged && !neighbor.isMine) {
                        queue.add(neighborRow to neighborCol)
                    }
                }
            }
        }

        return currentBoard
    }

    fun revealAllMinesOnLoss(board: Board, explodedRow: Int, explodedCol: Int): Board {
        var result = board
        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                val cell = board.cellAt(row, col)
                if (cell.isMine) {
                    val isExploded = row == explodedRow && col == explodedCol
                    result = result.updateCell(row, col) {
                        it.copy(isRevealed = true, isExploded = isExploded)
                    }
                }
            }
        }
        return result
    }

    fun autoFlagMinesOnWin(board: Board): Board {
        var result = board
        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                val cell = board.cellAt(row, col)
                if (cell.isMine && !cell.isFlagged) {
                    result = result.updateCell(row, col) { it.copy(isFlagged = true) }
                }
            }
        }
        return result
    }

    private fun buildSafeZone(rows: Int, cols: Int, safeRow: Int, safeCol: Int): Set<Pair<Int, Int>> {
        val zone = mutableSetOf<Pair<Int, Int>>()
        for (row in (safeRow - 1).coerceAtLeast(0)..(safeRow + 1).coerceAtMost(rows - 1)) {
            for (col in (safeCol - 1).coerceAtLeast(0)..(safeCol + 1).coerceAtMost(cols - 1)) {
                zone.add(row to col)
            }
        }
        return zone
    }

    private fun countAdjacentMines(minePositions: Set<Pair<Int, Int>>, row: Int, col: Int): Int {
        var count = 0
        for (dRow in -1..1) {
            for (dCol in -1..1) {
                if (dRow == 0 && dCol == 0) continue
                if ((row + dRow to col + dCol) in minePositions) count++
            }
        }
        return count
    }

    private fun neighbors(board: Board, row: Int, col: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (dRow in -1..1) {
            for (dCol in -1..1) {
                if (dRow == 0 && dCol == 0) continue
                val newRow = row + dRow
                val newCol = col + dCol
                if (newRow in 0 until board.rows && newCol in 0 until board.cols) {
                    result.add(newRow to newCol)
                }
            }
        }
        return result
    }
}
