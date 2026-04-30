package com.example.jedicouncilchallenge.presentation.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.jedicouncilchallenge.R
import com.example.jedicouncilchallenge.core.presentation.ObserveAsEvents
import com.example.jedicouncilchallenge.core.presentation.UiText
import com.example.jedicouncilchallenge.presentation.images.characterImageUrl
import com.example.jedicouncilchallenge.presentation.theme.StarWarsColors
import com.example.jedicouncilchallenge.presentation.theme.StarWarsTheme

@Composable
fun CharacterListRoot(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CharacterListEvent.NavigateToDetail -> onNavigateToDetail(event.characterId)
        }
    }

    CharacterListScreen(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSpeciesFilterChange = viewModel::onSpeciesFilterChange,
        onGenderFilterChange = viewModel::onGenderFilterChange,
        onSortOptionChange = viewModel::onSortOptionChange,
        onCharacterClick = viewModel::onCharacterClick,
        onToggleFavourite = viewModel::onToggleFavourite,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::loadInitialData
    )
}

@Composable
fun CharacterListScreen(
    state: CharacterListState,
    onSearchQueryChange: (String) -> Unit,
    onSpeciesFilterChange: (String?) -> Unit,
    onGenderFilterChange: (String?) -> Unit,
    onSortOptionChange: (CharacterSortOption) -> Unit,
    onCharacterClick: (Int) -> Unit,
    onToggleFavourite: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = StarWarsColors.Yellow)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchAndSortRow(
            query = state.searchQuery,
            selectedSort = state.sortOption,
            onQueryChange = onSearchQueryChange,
            onSortSelect = onSortOptionChange
        )

        if (state.availableSpecies.isNotEmpty() || state.availableGenders.isNotEmpty()) {
            FilterControlsRow(
                speciesOptions = state.availableSpecies,
                selectedSpecies = state.selectedSpecies,
                onSpeciesSelect = onSpeciesFilterChange,
                genderOptions = state.availableGenders,
                selectedGender = state.selectedGender,
                onGenderSelect = onGenderFilterChange
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.error != null -> {
                    val context = LocalContext.current
                    ErrorState(
                        message = state.error.asString(context),
                        onRetry = onRetry,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                state.displayedCharacters.isEmpty() -> {
                    EmptyState(modifier = Modifier.fillMaxWidth())
                }
                else -> {
                    val listState = rememberLazyListState()

                    // Trigger loadMore when the user scrolls within 3 items of the end
                    LaunchedEffect(listState, state.canLoadMore) {
                        snapshotFlow {
                            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        }.collect { lastVisibleIndex ->
                            if (state.canLoadMore &&
                                lastVisibleIndex != null &&
                                lastVisibleIndex >= listState.layoutInfo.totalItemsCount - 3
                            ) {
                                onLoadMore()
                            }
                        }
                    }

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.screen_characters),
                                modifier = Modifier.fillMaxWidth(),
                                color = StarWarsColors.Yellow,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black
                            )
                        }

                        items(state.displayedCharacters, key = { it.id }) { character ->
                            CharacterCard(
                                character = character,
                                isFavourite = character.id in state.favouriteCharacterIds,
                                onClick = { onCharacterClick(character.id) },
                                onToggleFavourite = { onToggleFavourite(character.id) }
                            )
                        }

                        if (state.canLoadMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = StarWarsColors.Yellow)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAndSortRow(
    query: String,
    selectedSort: CharacterSortOption,
    onQueryChange: (String) -> Unit,
    onSortSelect: (CharacterSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f)
        )
        SortMenu(
            selected = selectedSort,
            onSelect = onSortSelect
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                stringResource(R.string.search_hint_characters),
                color = Color.Black.copy(alpha = 0.55f)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = StarWarsColors.Black,
            unfocusedTextColor = StarWarsColors.Black,
            cursorColor = StarWarsColors.Yellow,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SortMenu(
    selected: CharacterSortOption,
    onSelect: (CharacterSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .clip(shape)
                .background(StarWarsColors.Yellow)
                .border(1.dp, StarWarsColors.Black.copy(alpha = 0.45f), shape)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.SortByAlpha,
                contentDescription = stringResource(R.string.cd_sort_characters),
                tint = StarWarsColors.Black,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = selected.label,
                color = StarWarsColors.Black,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            CharacterSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = StarWarsColors.Black,
                            fontWeight = if (selected == option) FontWeight.Black else FontWeight.Normal
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterControlsRow(
    speciesOptions: List<String>,
    selectedSpecies: String?,
    onSpeciesSelect: (String?) -> Unit,
    genderOptions: List<String>,
    selectedGender: String?,
    onGenderSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterMenuButton(
            label = stringResource(R.string.filter_label_species),
            options = speciesOptions,
            selected = selectedSpecies,
            onSelect = onSpeciesSelect,
            modifier = Modifier.weight(1f)
        )
        FilterMenuButton(
            label = stringResource(R.string.filter_label_gender),
            options = genderOptions,
            selected = selectedGender,
            onSelect = onGenderSelect,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FilterMenuButton(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    val isActive = selected != null
    val containerColor = if (isActive) StarWarsColors.Yellow else StarWarsColors.SurfaceOverlay
    val contentColor = if (isActive) StarWarsColors.Black else Color.White
    val borderColor =
        if (isActive) StarWarsColors.Yellow else StarWarsColors.Yellow.copy(alpha = 0.5f)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .background(containerColor)
                .border(1.dp, borderColor, shape)
                .clickable(enabled = options.isNotEmpty()) { expanded = true }
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = contentColor.copy(alpha = if (isActive) 0.7f else 0.85f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = selected ?: stringResource(R.string.filter_any),
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.filter_all, label),
                        color = StarWarsColors.Black,
                        fontWeight = if (selected == null) FontWeight.Black else FontWeight.Normal
                    )
                },
                onClick = {
                    expanded = false
                    onSelect(null)
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = StarWarsColors.Black,
                            fontWeight = if (selected == option) FontWeight.Black else FontWeight.Normal
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun CharacterCard(
    character: CharacterUi,
    isFavourite: Boolean,
    onClick: () -> Unit,
    onToggleFavourite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageShape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(214.dp)
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(imageShape)
                    .background(StarWarsColors.Black)
                    .border(1.dp, StarWarsColors.Yellow, imageShape),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onToggleFavourite,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(StarWarsColors.Black)
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(if (isFavourite) R.string.cd_remove_favourite else R.string.cd_add_favourite),
                    tint = if (isFavourite) StarWarsColors.Yellow else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = character.name.uppercase(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = StarWarsColors.Yellow,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            color = StarWarsColors.Error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = StarWarsColors.Yellow,
                contentColor = StarWarsColors.Black
            )
        ) {
            Text(stringResource(R.string.btn_retry), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.empty_no_characters),
        modifier = modifier.fillMaxWidth(),
        color = StarWarsColors.TextSecondary,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

private class CharacterListStatePreviewProvider : PreviewParameterProvider<CharacterListState> {
    override val values = sequenceOf(
        CharacterListState(
            isLoading = true
        ),
        CharacterListState(
            displayedCharacters = previewCharacters,
            availableSpecies = listOf("Human", "Droid", "Wookiee"),
            availableGenders = listOf("female", "male", "n/a"),
            sortOption = CharacterSortOption.NameAscending,
            favouriteCharacterIds = setOf(1, 3),
            canLoadMore = true
        ),
        CharacterListState(
            searchQuery = "jar jar",
            availableSpecies = listOf("Human", "Droid", "Wookiee"),
            availableGenders = listOf("female", "male", "n/a"),
            sortOption = CharacterSortOption.NameDescending
        ),
        CharacterListState(
            error = UiText.DynamicString("Unable to reach a galaxy far, far away."),
            availableSpecies = listOf("Human", "Droid", "Wookiee"),
            availableGenders = listOf("female", "male", "n/a")
        )
    )
}

private val previewCharacters = listOf(
    CharacterUi(
        id = 1,
        name = "Luke Skywalker",
        imageUrl = characterImageUrl(1),
        speciesName = "Human"
    ),
    CharacterUi(
        id = 2,
        name = "C-3PO",
        imageUrl = characterImageUrl(2),
        speciesName = "Droid"
    ),
    CharacterUi(
        id = 13,
        name = "Chewbacca",
        imageUrl = characterImageUrl(13),
        speciesName = "Wookiee"
    )
)

@Preview(
    name = "Character List States",
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun CharacterListScreenPreview(
    @PreviewParameter(CharacterListStatePreviewProvider::class) state: CharacterListState
) {
    StarWarsTheme {
        CharacterListScreen(
            state = state,
            onSearchQueryChange = {},
            onSpeciesFilterChange = {},
            onGenderFilterChange = {},
            onSortOptionChange = {},
            onCharacterClick = {},
            onToggleFavourite = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}
