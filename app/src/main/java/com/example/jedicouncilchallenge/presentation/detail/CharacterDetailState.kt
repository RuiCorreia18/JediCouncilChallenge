package com.example.jedicouncilchallenge.presentation.detail

import androidx.compose.runtime.Stable
import com.example.jedicouncilchallenge.core.presentation.UiText
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Starship

@Stable
data class CharacterDetailState(
    val character: Character? = null,
    val homeworld: Planet? = null,
    val starships: List<Starship> = emptyList(),
    val isFavourite: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
)
