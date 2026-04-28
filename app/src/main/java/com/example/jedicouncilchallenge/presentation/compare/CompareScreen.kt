package com.example.jedicouncilchallenge.presentation.compare

import android.text.style.UnderlineSpan
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.jedicouncilchallenge.presentation.theme.StarWarsColors
import com.example.jedicouncilchallenge.presentation.theme.StarWarsTheme

@Composable
fun CompareRoot(
    viewModel: CompareViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CompareScreen(
        state = state,
        onLeftSlotClick = viewModel::openLeftPicker,
        onRightSlotClick = viewModel::openRightPicker,
        onClearLeft = viewModel::clearLeft,
        onClearRight = viewModel::clearRight,
        onLeftQueryChange = viewModel::onLeftQueryChange,
        onRightQueryChange = viewModel::onRightQueryChange,
        onSelectLeft = viewModel::selectLeft,
        onSelectRight = viewModel::selectRight,
        onCloseLeftPicker = viewModel::closeLeftPicker,
        onCloseRightPicker = viewModel::closeRightPicker,
        onCompare = viewModel::compare,
        onBackFromCompare = viewModel::backFromCompare,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    state: CompareState,
    onLeftSlotClick: () -> Unit,
    onRightSlotClick: () -> Unit,
    onClearLeft: () -> Unit,
    onClearRight: () -> Unit,
    onLeftQueryChange: (String) -> Unit,
    onRightQueryChange: (String) -> Unit,
    onSelectLeft: (CompareCharacterUi) -> Unit,
    onSelectRight: (CompareCharacterUi) -> Unit,
    onCloseLeftPicker: () -> Unit,
    onCloseRightPicker: () -> Unit,
    onCompare: () -> Unit,
    onBackFromCompare: () -> Unit,
    onRetry: () -> Unit
) {
    if (state.isComparing && state.leftSelected != null && state.rightSelected != null) {
        BackHandler(onBack = onBackFromCompare)
        CompareResultScreen(
            left = state.leftSelected,
            right = state.rightSelected,
            onBack = onBackFromCompare
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = StarWarsColors.Yellow
            )
            state.error != null -> {
                val context = LocalContext.current
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = state.error.asString(context),
                        color = StarWarsColors.Error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StarWarsColors.Yellow,
                            contentColor = StarWarsColors.Black
                        )
                    ) {
                        Text("Retry", fontWeight = FontWeight.Bold)
                    }
                }
            }
            else -> ComparePickerContent(
                state = state,
                onLeftSlotClick = onLeftSlotClick,
                onRightSlotClick = onRightSlotClick,
                onClearLeft = onClearLeft,
                onClearRight = onClearRight,
                onCompare = onCompare
            )
        }
    }

    if (state.isLeftPickerOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onCloseLeftPicker,
            sheetState = sheetState,
            containerColor = Color(0xFF0D0D0D)
        ) {
            CharacterPickerSheet(
                query = state.leftQuery,
                suggestions = state.leftSuggestions,
                onQueryChange = onLeftQueryChange,
                onSelect = onSelectLeft,
                onDismiss = onCloseLeftPicker
            )
        }
    }

    if (state.isRightPickerOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onCloseRightPicker,
            sheetState = sheetState,
            containerColor = Color(0xFF0D0D0D)
        ) {
            CharacterPickerSheet(
                query = state.rightQuery,
                suggestions = state.rightSuggestions,
                onQueryChange = onRightQueryChange,
                onSelect = onSelectRight,
                onDismiss = onCloseRightPicker
            )
        }
    }
}

// ── Slot picker (first screen) ──────────────────────────────────────────────

@Composable
private fun ComparePickerContent(
    state: CompareState,
    onLeftSlotClick: () -> Unit,
    onRightSlotClick: () -> Unit,
    onClearLeft: () -> Unit,
    onClearRight: () -> Unit,
    onCompare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StarWarsLogoText()
        Spacer(Modifier.height(28.dp))

        CharacterSlot(character = state.leftSelected, onClick = onLeftSlotClick, onClear = onClearLeft)
        Spacer(Modifier.height(12.dp))

        Text(
            text = "VS",
            color = StarWarsColors.Yellow,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = 4.sp
        )
        Spacer(Modifier.height(12.dp))

        CharacterSlot(character = state.rightSelected, onClick = onRightSlotClick, onClear = onClearRight)

        if (state.leftSelected != null && state.rightSelected != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onCompare,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StarWarsColors.Yellow,
                    contentColor = StarWarsColors.Black
                )
            ) {
                Text(
                    text = "COMPARE",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StarWarsLogoText() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "STAR",
            color = StarWarsColors.Yellow,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 8.sp,
            lineHeight = 36.sp
        )
        Text(
            text = "WARS",
            color = StarWarsColors.Yellow,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 8.sp,
            lineHeight = 36.sp
        )
    }
}

@Composable
private fun CharacterSlot(
    character: CompareCharacterUi?,
    onClick: () -> Unit,
    onClear: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .background(StarWarsColors.SurfaceOverlay)
            .border(2.dp, StarWarsColors.Yellow, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (character == null) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Select character",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            )
        } else {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = character.name.uppercase(),
                    color = StarWarsColors.Yellow,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onClear,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(50))
                        .padding(2.dp)
                )
            }
        }
    }
}

// ── Comparison result (second screen) ───────────────────────────────────────

@Composable
private fun CompareResultScreen(
    left: CompareCharacterUi,
    right: CompareCharacterUi,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Photos row with back button overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = left.imageUrl,
                    contentDescription = left.name,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                AsyncImage(
                    model = right.imageUrl,
                    contentDescription = right.name,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = StarWarsColors.Yellow
                )
            }
        }

        // Names row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = left.name.uppercase(),
                color = StarWarsColors.Yellow,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = right.name.uppercase(),
                color = StarWarsColors.Yellow,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CompareSection(title = "BIOGRAPHICAL INFORMATION") {
                CompareStat("BIRTH YEAR", left.birthYear, right.birthYear)
                CompareStat("FILMS", left.filmCount.toString(), right.filmCount.toString())
                CompareStat("STARSHIPS", left.starshipCount.toString(), right.starshipCount.toString())
            }

            Spacer(Modifier.height(8.dp))

            CompareSection(title = "PHYSICAL DESCRIPTION") {
                CompareStat("SPECIES", left.speciesName, right.speciesName)
                CompareStat("GENDER", left.gender, right.gender)
                CompareStat("MASS", left.mass, right.mass)
                CompareStat("HEIGHT", left.height, right.height)
                CompareStat("HAIR", left.hairColor, right.hairColor)
                CompareStat("EYES", left.eyeColor, right.eyeColor)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CompareSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                color = StarWarsColors.Yellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .drawBehind {
                        drawLine(
                            color = StarWarsColors.Yellow,
                            start = Offset(0f, size.height - 4.dp.toPx()),
                            end = Offset(size.width, size.height - 4.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(bottom = 2.dp)
            )
        }
        content()
    }
}

@Composable
private fun CompareStat(label: String, leftValue: String, rightValue: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = StarWarsColors.Yellow,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = leftValue,
                color = StarWarsColors.Yellow,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = rightValue,
                color = StarWarsColors.Yellow,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Character picker bottom sheet ────────────────────────────────────────────

@Composable
private fun CharacterPickerSheet(
    query: String,
    suggestions: List<CompareCharacterUi>,
    onQueryChange: (String) -> Unit,
    onSelect: (CompareCharacterUi) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "SELECT CHARACTER",
            color = StarWarsColors.Yellow,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search character…", color = StarWarsColors.TextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = StarWarsColors.SurfaceOverlay,
                unfocusedContainerColor = StarWarsColors.SurfaceOverlay,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = StarWarsColors.Yellow,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(360.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(suggestions, key = { it.id }) { character ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(character) }
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = character.imageUrl,
                        contentDescription = character.name,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StarWarsColors.SurfaceOverlay),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = character.name,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CompareEmptyPreview() {
    StarWarsTheme {
        CompareScreen(
            state = CompareState(),
            onLeftSlotClick = {}, onRightSlotClick = {},
            onClearLeft = {}, onClearRight = {},
            onLeftQueryChange = {}, onRightQueryChange = {},
            onSelectLeft = {}, onSelectRight = {},
            onCloseLeftPicker = {}, onCloseRightPicker = {},
            onCompare = {}, onBackFromCompare = {}, onRetry = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CompareResultPreview() {
    StarWarsTheme {
        CompareResultScreen(
            left = previewLuke,
            right = previewVader,
            onBack = {}
        )
    }
}

private val previewLuke = CompareCharacterUi(
    id = 1, name = "Luke Skywalker", speciesName = "Human",
    imageUrl = "https://starwars-visualguide.com/assets/img/characters/1.jpg",
    gender = "Male", birthYear = "19BBY", height = "172", mass = "77",
    hairColor = "Blond", eyeColor = "Blue", starshipCount = 2, filmCount = 4
)

private val previewVader = CompareCharacterUi(
    id = 4, name = "Darth Vader", speciesName = "Human",
    imageUrl = "https://starwars-visualguide.com/assets/img/characters/4.jpg",
    gender = "Male", birthYear = "41.9BBY", height = "202", mass = "136",
    hairColor = "None", eyeColor = "Yellow", starshipCount = 1, filmCount = 4
)
