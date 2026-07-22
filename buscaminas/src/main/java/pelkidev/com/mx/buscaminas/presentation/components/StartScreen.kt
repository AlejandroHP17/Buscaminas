package pelkidev.com.mx.buscaminas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import pelkidev.com.mx.buscaminas.R
import pelkidev.com.mx.buscaminas.domain.model.Difficulty
import pelkidev.com.mx.buscaminas.domain.model.SavedGameInfo

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    StartScreen(
        selectedDifficulty = Difficulty.PERSONALIZED,
        customCols = 9,
        customRows = 9,
        customMineCount = 10,
        hasSavedGame = false,
        savedGameInfo = SavedGameInfo(
            difficulty = Difficulty.BEGINNER,
            elapsedSeconds = 1,
        ),
        onDifficultySelected = {},
        onCustomColsChanged = {},
        onCustomRowsChanged = {},
        onCustomMineCountChanged = {},
        onStartGame = {},
        onContinueGame = {},
        onExit = {},
        modifier = Modifier,
    )
}

@Composable
fun StartScreen(
    selectedDifficulty: Difficulty,
    customCols: Int,
    customRows: Int,
    customMineCount: Int,
    hasSavedGame: Boolean,
    savedGameInfo: SavedGameInfo?,
    onDifficultySelected: (Difficulty) -> Unit,
    onCustomColsChanged: (Int) -> Unit,
    onCustomRowsChanged: (Int) -> Unit,
    onCustomMineCountChanged: (Int) -> Unit,
    onStartGame: () -> Unit,
    onContinueGame: () -> Unit,
    onExit: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
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
                .verticalScroll(rememberScrollState())
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
                        customCols = customCols,
                        customRows = customRows,
                        customMineCount = customMineCount,
                        onSelected = { onDifficultySelected(difficulty) },
                        onCustomColsChanged = onCustomColsChanged,
                        onCustomRowsChanged = onCustomRowsChanged,
                        onCustomMineCountChanged = onCustomMineCountChanged,
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
    customCols: Int,
    customRows: Int,
    customMineCount: Int,
    onSelected: () -> Unit,
    onCustomColsChanged: (Int) -> Unit,
    onCustomRowsChanged: (Int) -> Unit,
    onCustomMineCountChanged: (Int) -> Unit,
) {
    val descriptionCols = if (difficulty == Difficulty.PERSONALIZED) customCols else difficulty.cols
    val descriptionRows = if (difficulty == Difficulty.PERSONALIZED) customRows else difficulty.rows
    val descriptionMines =
        if (difficulty == Difficulty.PERSONALIZED) customMineCount else difficulty.mineCount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = difficulty.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = stringResource(
                    R.string.buscaminas_difficulty_description,
                    descriptionCols,
                    descriptionRows,
                    descriptionMines,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (difficulty == Difficulty.PERSONALIZED && isSelected) {
                Spacer(modifier = Modifier.height(12.dp))
                CustomBoardFields(
                    customCols = customCols,
                    customRows = customRows,
                    customMineCount = customMineCount,
                    onCustomColsChanged = onCustomColsChanged,
                    onCustomRowsChanged = onCustomRowsChanged,
                    onCustomMineCountChanged = onCustomMineCountChanged,
                )
            }
        }
    }
}

@Composable
private fun CustomBoardFields(
    customCols: Int,
    customRows: Int,
    customMineCount: Int,
    onCustomColsChanged: (Int) -> Unit,
    onCustomRowsChanged: (Int) -> Unit,
    onCustomMineCountChanged: (Int) -> Unit,
) {
    val maxMines = (customRows * customCols - 1).coerceAtLeast(Difficulty.CUSTOM_MIN_MINES)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CustomNumberStepper(
            label = stringResource(R.string.buscaminas_custom_base),
            value = customCols,
            minValue = Difficulty.CUSTOM_MIN_SIZE,
            maxValue = Difficulty.CUSTOM_MAX_SIZE,
            onValueChange = onCustomColsChanged,
        )
        CustomNumberStepper(
            label = stringResource(R.string.buscaminas_custom_height),
            value = customRows,
            minValue = Difficulty.CUSTOM_MIN_SIZE,
            maxValue = Difficulty.CUSTOM_MAX_SIZE,
            onValueChange = onCustomRowsChanged,
        )
        CustomNumberStepper(
            label = stringResource(R.string.buscaminas_custom_mines),
            value = customMineCount,
            minValue = Difficulty.CUSTOM_MIN_MINES,
            maxValue = maxMines,
            onValueChange = onCustomMineCountChanged,
        )
    }
}

@Composable
private fun CustomNumberStepper(
    label: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(64.dp),
        )
        RepeatingIconButton(
            onClick = { onValueChange(value - 1) },
            enabled = value > minValue,
        ) {
            Text(
                text = "−",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp),
        )
        RepeatingIconButton(
            onClick = { onValueChange(value + 1) },
            enabled = value < maxValue,
        ) {
            Text(
                text = "+",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RepeatingIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOnClick by rememberUpdatedState(onClick)
    val currentEnabled by rememberUpdatedState(enabled)
    val wasAutoRepeated = remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (!isPressed || !currentEnabled) return@LaunchedEffect

        wasAutoRepeated.value = false
        currentOnClick()
        wasAutoRepeated.value = true
        delay(AUTO_REPEAT_INITIAL_DELAY_MS)

        while (isActive) {
            if (!currentEnabled) break
            currentOnClick()
            delay(AUTO_REPEAT_INTERVAL_MS)
        }
    }

    IconButton(
        onClick = {
            if (!wasAutoRepeated.value) {
                currentOnClick()
            }
        },
        enabled = enabled,
        interactionSource = interactionSource,
        content = content,
    )
}

private const val AUTO_REPEAT_INITIAL_DELAY_MS = 400L
private const val AUTO_REPEAT_INTERVAL_MS = 60L
