package com.example.jedicouncilchallenge.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.jedicouncilchallenge.R
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Starship
import com.example.jedicouncilchallenge.presentation.images.characterImageUrl
import com.example.jedicouncilchallenge.presentation.theme.StarWarsColors
import com.example.jedicouncilchallenge.presentation.theme.StarWarsTheme

@Composable
fun CharacterDetailRoot(
    characterId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(characterId) {
        viewModel.loadCharacter(characterId)
    }

    CharacterDetailScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onToggleFavourite = viewModel::toggleFavourite,
        onRetry = viewModel::retry
    )
}

@Composable
fun CharacterDetailScreen(
    state: CharacterDetailState,
    onNavigateBack: () -> Unit,
    onToggleFavourite: () -> Unit,
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
            state.character != null -> {
                CharacterDetailContent(
                    state = state,
                    onNavigateBack = onNavigateBack,
                    onToggleFavourite = onToggleFavourite
                )
            }
        }
    }
}

@Composable
private fun CharacterDetailContent(
    state: CharacterDetailState,
    onNavigateBack: () -> Unit,
    onToggleFavourite: () -> Unit
) {
    val character = state.character ?: return
    val screenShape = RoundedCornerShape(28.dp)
    val imageShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .clip(screenShape)
            .background(StarWarsColors.Black)
            .border(1.dp, StarWarsColors.Yellow.copy(alpha = 0.55f), screenShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = characterImageUrl(character.id),
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(imageShape)
                    .background(StarWarsColors.Black),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 18.dp)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(StarWarsColors.Black)
                    .border(1.dp, StarWarsColors.Yellow.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = StarWarsColors.Yellow
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircleIconButton(
                    icon = if (state.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(if (state.isFavourite) R.string.cd_remove_favourite else R.string.cd_add_favourite),
                    onClick = onToggleFavourite
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = character.name.uppercase(),
                modifier = Modifier.fillMaxWidth(),
                color = StarWarsColors.Yellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            DetailSectionTitle(stringResource(R.string.section_biographical))
            CenteredFactRows(
                rows = listOf(
                    stringResource(R.string.label_homeworld) to (state.homeworld?.name
                        ?: stringResource(R.string.value_unknown)),
                    stringResource(R.string.label_born) to character.birthYear.displayValue(),
                    stringResource(R.string.label_films) to character.filmIds.size.toString()
                )
            )

            DetailSectionTitle(stringResource(R.string.section_physical))
            CenteredFactRows(
                rows = listOf(
                    stringResource(R.string.label_gender) to character.gender.displayValue(),
                    stringResource(R.string.label_height) to character.height.withUnit("meters"),
                    stringResource(R.string.label_mass) to character.mass.withUnit("kilograms"),
                    stringResource(R.string.label_eyes) to character.eyeColor.displayValue(),
                    stringResource(R.string.label_hair) to character.hairColor.displayValue()
                )
            )

            DetailSectionTitle(stringResource(R.string.section_story))
            StoryText(
                text = character.storySummary(
                    homeworldName = state.homeworld?.name,
                    starshipCount = state.starships.size
                )
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(StarWarsColors.Black)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = StarWarsColors.Yellow,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
private fun DetailSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        color = StarWarsColors.Yellow,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun CenteredFactRows(rows: List<Pair<String, String>>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        rows.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${label.uppercase()}:",
                    modifier = Modifier.weight(1f),
                    color = StarWarsColors.Yellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value.uppercase(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp),
                    color = StarWarsColors.Yellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StoryText(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.fillMaxWidth(),
        color = StarWarsColors.Yellow,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.15f
    )
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
            Text(stringResource(R.string.btn_retry), fontWeight = FontWeight.Bold)
        }
    }
}

private fun Character.storySummary(homeworldName: String?, starshipCount: Int): String {
    val homeworld = homeworldName ?: "an unknown homeworld"
    val starshipText = when (starshipCount) {
        0 -> "No known starships are listed in the archive."
        1 -> "One known starship is listed in the archive."
        else -> "$starshipCount known starships are listed in the archive."
    }

    return "$name is a ${gender.displayValue()} figure from $homeworld. " +
        "Archive records list a birth year of ${birthYear.displayValue()}, " +
        "a height of ${height.withUnit("meters")}, and a mass of ${mass.withUnit("kilograms")}. " +
        starshipText
}

private fun String.displayValue(): String =
    if (equals("unknown", ignoreCase = true)) "Unknown" else replaceFirstChar { it.uppercase() }

private fun String.withUnit(unit: String): String =
    if (equals("unknown", ignoreCase = true)) "Unknown" else "$this $unit"

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CharacterDetailScreenPreview() {
    StarWarsTheme {
        CharacterDetailScreen(
            state = CharacterDetailState(
                character = Character(
                    id = 1,
                    name = "Luke Skywalker",
                    gender = "male",
                    birthYear = "19BBY",
                    height = "1.72",
                    mass = "73",
                    hairColor = "blond",
                    skinColor = "fair",
                    eyeColor = "blue",
                    speciesIds = emptyList(),
                    filmIds = listOf(1, 2, 3, 6),
                    starshipIds = listOf(12, 22),
                    homeworldId = 1
                ),
                homeworld = Planet(1, "Tatooine", "arid", "desert", "200000"),
                starships = listOf(
                    Starship(12, "X-wing", "T-65 X-wing", "Incom Corporation", "Starfighter")
                ),
                isFavourite = true
            ),
            onNavigateBack = {},
            onToggleFavourite = {},
            onRetry = {}
        )
    }
}
