package pelkidev.com.mx.buscaminas.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pelkidev.com.mx.buscaminas.domain.model.SavedGameState
import pelkidev.com.mx.buscaminas.domain.repository.GameSaveRepository

class GameSaveRepositoryImpl(
    context: Context,
) : GameSaveRepository {

    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun saveGame(state: SavedGameState) {
        withContext(Dispatchers.IO) {
            preferences.edit()
                .putString(KEY_SAVED_GAME, GameSaveMapper.toJson(state))
                .apply()
        }
    }

    override suspend fun loadGame(): SavedGameState? {
        return withContext(Dispatchers.IO) {
            val json = preferences.getString(KEY_SAVED_GAME, null) ?: return@withContext null
            GameSaveMapper.fromJson(json)?.takeIf { it.isResumable }
        }
    }

    override suspend fun clearSavedGame() {
        withContext(Dispatchers.IO) {
            preferences.edit()
                .remove(KEY_SAVED_GAME)
                .apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "buscaminas_game_save"
        private const val KEY_SAVED_GAME = "saved_game"
    }
}
