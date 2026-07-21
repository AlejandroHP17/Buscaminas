package pelkidev.com.mx.buscaminas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import pelkidev.com.mx.buscaminas.presentation.MinesweeperScreen
import pelkidev.com.mx.buscaminas.ui.theme.BuscaminasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuscaminasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MinesweeperScreen()
                }
            }
        }
    }
}
