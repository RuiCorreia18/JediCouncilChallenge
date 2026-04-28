package com.example.jedicouncilchallenge.presentation.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.jedicouncilchallenge.core.presentation.ObserveAsEvents
import com.example.jedicouncilchallenge.core.presentation.UiText
import com.example.jedicouncilchallenge.presentation.theme.StarWarsColors
import com.example.jedicouncilchallenge.presentation.theme.StarWarsTheme

@Composable
fun FavouritesRoot(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FavouritesEvent.NavigateToDetail -> onNavigateToDetail(event.characterId)
        }
    }

    FavouritesScreen(
        state = state,
        onCharacterClick = viewModel::onCharacterClick,
        onRemoveFavourite = viewModel::onRemoveFavourite,
        onRetry = viewModel::retry
    )
}

@Composable
fun FavouritesScreen(
    state: FavouritesState,
    onCharacterClick: (Int) -> Unit,
    onRemoveFavourite: (Int) -> Unit,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
            state.characters.isEmpty() -> {
                EmptyState(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    item {
                        Text(
                            text = "FAVOURITES",
                            modifier = Modifier.fillMaxWidth(),
                            color = StarWarsColors.Yellow,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black
                        )
                    }

                    items(state.characters, key = { it.id }) { character ->
                        FavouriteCharacterCard(
                            character = character,
                            onClick = { onCharacterClick(character.id) },
                            onRemoveFavourite = { onRemoveFavourite(character.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavouriteCharacterCard(
    character: FavouriteCharacterUi,
    onClick: () -> Unit,
    onRemoveFavourite: () -> Unit,
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
                onClick = onRemoveFavourite,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(StarWarsColors.Black)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove from favourites",
                    tint = StarWarsColors.Yellow,
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
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "NO FAVOURITES",
            color = StarWarsColors.Yellow,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Mark characters with the star to keep them here.",
            color = StarWarsColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium
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

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun FavouritesScreenPreview() {
    StarWarsTheme {
        FavouritesScreen(
            state = FavouritesState(
                characters = listOf(
                    FavouriteCharacterUi(
                        id = 1,
                        name = "Luke Skywalker",
                        imageUrl = "https://starwars-visualguide.com/assets/img/characters/1.jpg"
                    ),
                    FavouriteCharacterUi(
                        id = 4,
                        name = "Darth Vader",
                        imageUrl = "https://starwars-visualguide.com/assets/img/characters/4.jpg"
                    )
                )
            ),
            onCharacterClick = {},
            onRemoveFavourite = {},
            onRetry = {}
        )
    }
}
