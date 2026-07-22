package pelkidev.com.mx.buscaminas.domain.model

enum class Difficulty(
    val rows: Int,
    val cols: Int,
    val mineCount: Int,
    val displayName: String,
) {
    BEGINNER(rows = 9, cols = 9, mineCount = 10, displayName = "Principiante"),
    INTERMEDIATE(rows = 16, cols = 16, mineCount = 40, displayName = "Intermedio"),
    EXPERT(rows = 16, cols = 30, mineCount = 99, displayName = "Experto"),
    PERSONALIZED(rows = 9, cols = 9, mineCount = 10, displayName = "Personalizado"),
    ;

    companion object {
        const val CUSTOM_MIN_SIZE = 5
        const val CUSTOM_MAX_SIZE = 30
        const val CUSTOM_MIN_MINES = 1
    }
}
