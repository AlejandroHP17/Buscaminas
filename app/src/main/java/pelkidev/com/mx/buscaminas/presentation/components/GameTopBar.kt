package pelkidev.com.mx.buscaminas.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pelkidev.com.mx.buscaminas.domain.model.GameStatus

@Composable
fun GameTopBar(
    minesRemaining: Int,
    elapsedSeconds: Int,
    gameStatus: GameStatus,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CounterDisplay(
                value = minesRemaining.coerceAtMost(999),
                modifier = Modifier.weight(1f),
            )

            Surface(
                onClick = onRestart,
                interactionSource = interactionSource,
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(52.dp),
            ) {
                Text(
                    text = faceEmoji(gameStatus, isPressed),
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }

            CounterDisplay(
                value = elapsedSeconds.coerceAtMost(999),
                modifier = Modifier.weight(1f),
                alignEnd = true,
            )
        }
    }
}

@Composable
private fun CounterDisplay(
    value: Int,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false,
) {
    Text(
        text = value.toString().padStart(3, '0'),
        modifier = modifier,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.error,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
    )
}

private fun faceEmoji(gameStatus: GameStatus, isPressed: Boolean): String = when {
    isPressed -> "😮"
    gameStatus == GameStatus.LOST -> "😵"
    gameStatus == GameStatus.WON -> "😎"
    else -> "🙂"
}
