package pelkidev.com.mx.buscaminas.domain.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Difficulty

class MinesweeperEngineTest {

    @Test
    fun firstClickSafeZone_neverContainsMine() {
        val board = Board.empty(9, 9, 10)
        val generated = MinesweeperEngine.generateMines(board, safeRow = 4, safeCol = 4)

        for (row in 3..5) {
            for (col in 3..5) {
                assertFalse(generated.cellAt(row, col).isMine)
            }
        }
    }

    @Test
    fun generateMines_placesCorrectMineCount() {
        val board = Board.empty(9, 9, 10)
        val generated = MinesweeperEngine.generateMines(board, safeRow = 0, safeCol = 0)
        val mineCount = generated.cells.flatten().count { it.isMine }

        assertEquals(10, mineCount)
    }

    @Test
    fun revealEmptyCell_cascadesToNeighbors() {
        val board = Board.empty(3, 3, 0)
        val generated = MinesweeperEngine.generateMines(board, safeRow = 1, safeCol = 1)
        val result = MinesweeperEngine.revealCell(generated, 1, 1)

        result.board.cells.flatten().forEach { cell ->
            assertTrue(cell.isRevealed)
        }
        assertFalse(result.hitMine)
        assertTrue(result.won)
    }

    @Test
    fun toggleFlag_increasesFlagCount() {
        val board = Board.empty(9, 9, 10)
        val flagged = MinesweeperEngine.toggleFlag(board, 0, 0)

        assertTrue(flagged.cellAt(0, 0).isFlagged)
        assertEquals(1, flagged.flagCount)
    }

    @Test
    fun expertBoard_hasCorrectDimensions() {
        val difficulty = Difficulty.EXPERT
        val board = Board.empty(difficulty.rows, difficulty.cols, difficulty.mineCount)

        assertEquals(16, board.rows)
        assertEquals(30, board.cols)
        assertEquals(99, board.mineCount)
    }
}
