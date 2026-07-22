package pelkidev.com.mx.buscaminas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pelkidev.com.mx.buscaminas.R
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.SavedGameInfo

@Composable
fun StartScreen(
    selectedDifficulty: Difficulty,
    hasSavedGame: Boolean,
    savedGameInfo: SavedGameInfo?,
    onDifficultySelected: (Difficulty) -> Unit,
    onStartGame: () -> Unit,
    onContinueGame: () -> Unit,
    onExit: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (onExit != null) {
            IconButton(
                onClick = onExit,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp),
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_revert),
                    contentDescription = stringResource(R.string.buscaminas_back),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
        Text(
            text = "💣",
            fontSize = 64.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.buscaminas_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.buscaminas_start_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Difficulty.entries.forEach { difficulty ->
                DifficultyOption(
                    difficulty = difficulty,
                    isSelected = difficulty == selectedDifficulty,
                    onSelected = { onDifficultySelected(difficulty) },
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (hasSavedGame && savedGameInfo != null) {
            OutlinedButton(
                onClick = onContinueGame,
                modifier = Modifier.fillMaxWidth(0.7f),
            ) {
                Text(
                    text = stringResource(
                        R.string.buscaminas_continue_game,
                        savedGameInfo.difficulty.displayName,
                        formatElapsedTime(savedGameInfo.elapsedSeconds),
                    ),
                    modifier = Modifier.padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(0.7f),
        ) {
            Text(
                text = stringResource(R.string.buscaminas_start_game),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        }
    }
}

private fun formatElapsedTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainingSeconds)
}

@Composable
private fun DifficultyOption(
    difficulty: Difficulty,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
        )
        Column {
            Text(
                text = difficulty.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = stringResource(
                    R.string.buscaminas_difficulty_description,
                    difficulty.cols,
                    difficulty.rows,
                    difficulty.mineCount,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
