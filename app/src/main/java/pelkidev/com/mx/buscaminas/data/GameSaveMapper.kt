package pelkidev.com.mx.buscaminas.data

import org.json.JSONArray
import org.json.JSONObject
import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Cell
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.GameStatus
import pelkidev.com.mx.buscaminas.domain.model.SavedGameState

object GameSaveMapper {

    fun toJson(state: SavedGameState): String {
        val board = state.board
        val cellsArray = JSONArray()
        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                val cell = board.cellAt(row, col)
                cellsArray.put(
                    JSONObject().apply {
                        put("isMine", cell.isMine)
                        put("isRevealed", cell.isRevealed)
                        put("isFlagged", cell.isFlagged)
                        put("adjacentMines", cell.adjacentMines)
                        put("isExploded", cell.isExploded)
                    },
                )
            }
        }

        return JSONObject().apply {
            put("difficulty", state.difficulty.name)
            put("gameStatus", state.gameStatus.name)
            put("elapsedSeconds", state.elapsedSeconds)
            put("rows", board.rows)
            put("cols", board.cols)
            put("mineCount", board.mineCount)
            put("isGenerated", board.isGenerated)
            put("cells", cellsArray)
        }.toString()
    }

    fun fromJson(json: String): SavedGameState? {
        return try {
            val root = JSONObject(json)
            val difficulty = Difficulty.valueOf(root.getString("difficulty"))
            val gameStatus = GameStatus.valueOf(root.getString("gameStatus"))
            val elapsedSeconds = root.getInt("elapsedSeconds")
            val rows = root.getInt("rows")
            val cols = root.getInt("cols")
            val mineCount = root.getInt("mineCount")
            val isGenerated = root.getBoolean("isGenerated")
            val cellsArray = root.getJSONArray("cells")

            if (cellsArray.length() != rows * cols) return null

            val cells = List(rows) { row ->
                List(cols) { col ->
                    val index = row * cols + col
                    val cellJson = cellsArray.getJSONObject(index)
                    Cell(
                        row = row,
                        col = col,
                        isMine = cellJson.getBoolean("isMine"),
                        isRevealed = cellJson.getBoolean("isRevealed"),
                        isFlagged = cellJson.getBoolean("isFlagged"),
                        adjacentMines = cellJson.getInt("adjacentMines"),
                        isExploded = cellJson.getBoolean("isExploded"),
                    )
                }
            }

            SavedGameState(
                difficulty = difficulty,
                board = Board(
                    rows = rows,
                    cols = cols,
                    mineCount = mineCount,
                    cells = cells,
                    isGenerated = isGenerated,
                ),
                gameStatus = gameStatus,
                elapsedSeconds = elapsedSeconds,
            )
        } catch (_: Exception) {
            null
        }
    }
}
