package pelkidev.com.mx.buscaminas

import androidx.compose.runtime.Composable
import pelkidev.com.mx.buscaminas.presentation.MinesweeperScreen
import pelkidev.com.mx.buscaminas.ui.theme.BuscaminasTheme
import pelkidev.com.mx.minijuegos.sdk.GameModule

object BuscaminasGameModule : GameModule {
    override val id: String = "buscaminas"
    override val title: String = "Buscaminas"
    override val description: String = "Encuentra todas las minas sin explotar ninguna"

    @Composable
    override fun Entry(onExit: () -> Unit) {
        BuscaminasTheme {
            MinesweeperScreen(onExit = onExit)
        }
    }
}
