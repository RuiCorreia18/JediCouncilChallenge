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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.jedicouncilchallenge.core.presentation.ObserveAsEvents
import com.example.jedicouncilchallenge.core.presentation.UiText
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
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = state.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        if (state.availableSpecies.isNotEmpty()) {
            FilterRow(
                label = "Species",
                options = state.availableSpecies,
                selected = state.selectedSpecies,
                onSelect = onSpeciesFilterChange
            )
        }

        if (state.availableGenders.isNotEmpty()) {
            FilterRow(
                label = "Gender",
                options = state.availableGenders,
                selected = state.selectedGender,
                onSelect = onGenderFilterChange
            )
        }

        SortControl(
            selected = state.sortOption,
            onSelect = onSortOptionChange
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = StarWarsColors.Yellow
                    )
                }
                state.error != null -> {
                    val context = LocalContext.current
                    ErrorState(
                        message = state.error.asString(context),
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.displayedCharacters.isEmpty() -> {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        item {
                            Text(
                                text = "CHARACTERS",
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
                                Button(
                                    onClick = onLoadMore,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StarWarsColors.Yellow,
                                        contentColor = StarWarsColors.Black
                                    )
                                ) {
                                    Text("Load more", fontWeight = FontWeight.Bold)
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
private fun SortControl(
    selected: CharacterSortOption,
    onSelect: (CharacterSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape)
            .background(StarWarsColors.SurfaceOverlay)
            .border(1.dp, StarWarsColors.Yellow.copy(alpha = 0.55f), shape)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.SortByAlpha,
            contentDescription = null,
            tint = StarWarsColors.Yellow,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(20.dp)
        )
        Text(
            text = "Name",
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        CharacterSortOption.entries.forEach { option ->
            SortSegment(
                option = option,
                selected = selected == option,
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
private fun SortSegment(
    option: CharacterSortOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Text(
        text = option.label,
        modifier = modifier
            .padding(start = 4.dp)
            .clip(shape)
            .background(if (selected) StarWarsColors.Yellow else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) StarWarsColors.Yellow else StarWarsColors.TextSecondary,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        color = if (selected) StarWarsColors.Black else Color.White,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black,
        maxLines = 1
    )
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
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search characters…", color = StarWarsColors.TextSecondary) },
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
}

@Composable
private fun FilterRow(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("All $label") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StarWarsColors.Yellow,
                    selectedLabelColor = StarWarsColors.Black
                )
            )
        }
        items(options) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StarWarsColors.Yellow,
                    selectedLabelColor = StarWarsColors.Black
                )
            )
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
                    .height(190.dp)
                    .clip(imageShape)
                    .border(1.dp, StarWarsColors.Yellow, imageShape),
                contentScale = ContentScale.Crop
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
                    contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
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
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
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

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Text(
        text = "No characters found",
        modifier = modifier,
        color = StarWarsColors.TextSecondary,
        style = MaterialTheme.typography.bodyLarge
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
        imageUrl = "https://starwars-visualguide.com/assets/img/characters/1.jpg",
        speciesName = "Human"
    ),
    CharacterUi(
        id = 2,
        name = "C-3PO",
        imageUrl = "https://starwars-visualguide.com/assets/img/characters/2.jpg",
        speciesName = "Droid"
    ),
    CharacterUi(
        id = 13,
        name = "Chewbacca",
        imageUrl = "https://starwars-visualguide.com/assets/img/characters/13.jpg",
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
