package com.example.jedicouncilchallenge.presentation.compare

import androidx.compose.runtime.Stable
import com.example.jedicouncilchallenge.core.presentation.UiText

@Stable
data class CompareState(
    val leftQuery: String = "",
    val rightQuery: String = "",
    val leftSelected: CompareCharacterUi? = null,
    val rightSelected: CompareCharacterUi? = null,
    val leftSuggestions: List<CompareCharacterUi> = emptyList(),
    val rightSuggestions: List<CompareCharacterUi> = emptyList(),
    val isLeftPickerOpen: Boolean = false,
    val isRightPickerOpen: Boolean = false,
    val isComparing: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
)

data class CompareCharacterUi(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val speciesName: String,
    val gender: String,
    val birthYear: String,
    val height: String,
    val mass: String,
    val hairColor: String,
    val eyeColor: String,
    val starshipCount: Int,
    val filmCount: Int
)
