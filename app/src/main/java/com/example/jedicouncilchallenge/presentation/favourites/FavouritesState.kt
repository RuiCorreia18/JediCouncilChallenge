package com.example.jedicouncilchallenge.presentation.favourites

import androidx.compose.runtime.Stable
import com.example.jedicouncilchallenge.core.presentation.UiText

@Stable
data class FavouritesState(
    val characters: List<FavouriteCharacterUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)

data class FavouriteCharacterUi(
    val id: Int,
    val name: String,
    val imageUrl: String
)
