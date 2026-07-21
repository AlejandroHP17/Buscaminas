package pelkidev.com.mx.buscaminas.domain.model

data class SavedGameState(
    val difficulty: Difficulty,
    val board: Board,
    val gameStatus: GameStatus,
    val elapsedSeconds: Int,
) {
    val isResumable: Boolean
        get() = gameStatus == GameStatus.NOT_STARTED || gameStatus == GameStatus.PLAYING
}

data class SavedGameInfo(
    val difficulty: Difficulty,
    val elapsedSeconds: Int,
)
