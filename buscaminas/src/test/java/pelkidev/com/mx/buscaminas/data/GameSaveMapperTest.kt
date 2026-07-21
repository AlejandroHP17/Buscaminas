package pelkidev.com.mx.buscaminas.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import pelkidev.com.mx.buscaminas.domain.model.Board
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.GameStatus
import pelkidev.com.mx.buscaminas.domain.model.SavedGameState

class GameSaveMapperTest {

    @Test
    fun fromJson_invalidPayload_returnsNull() {
        assertNull(GameSaveMapper.fromJson("not-json"))
        assertNull(GameSaveMapper.fromJson("{}"))
    }

    @Test
    fun savedGameState_isResumable_onlyForActiveGames() {
        val board = Board.empty(9, 9, 10)
        val playing = SavedGameState(
            difficulty = Difficulty.BEGINNER,
            board = board,
            gameStatus = GameStatus.PLAYING,
            elapsedSeconds = 0,
        )
        val won = playing.copy(gameStatus = GameStatus.WON)

        assertTrue(playing.isResumable)
        assertFalse(won.isResumable)
    }
}
