package pelkidev.com.mx.buscaminas.domain.repository

import pelkidev.com.mx.buscaminas.domain.model.SavedGameState

interface GameSaveRepository {
    suspend fun saveGame(state: SavedGameState)
    suspend fun loadGame(): SavedGameState?
    suspend fun clearSavedGame()
}
