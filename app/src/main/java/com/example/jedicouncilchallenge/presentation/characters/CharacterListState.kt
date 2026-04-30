package com.example.jedicouncilchallenge.presentation.characters

import androidx.compose.runtime.Stable
import com.example.jedicouncilchallenge.core.presentation.UiText

@Stable
data class CharacterListState(
    val displayedCharacters: List<CharacterUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val searchQuery: String = "",
    val selectedSpecies: String? = null,
    val selectedGender: String? = null,
    val sortOption: CharacterSortOption = CharacterSortOption.NameAscending,
    val availableSpecies: List<String> = emptyList(),
    val availableGenders: List<String> = emptyList(),
    val favouriteCharacterIds: Set<Int> = emptySet(),
    val canLoadMore: Boolean = false
)

enum class CharacterSortOption(val label: String) {
    NameAscending("Name A-Z"),
    NameDescending("Name Z-A"),
    BirthYearAscending("Year Oldest"),
    BirthYearDescending("Year Newest")
}

data class CharacterUi(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val speciesName: String
)
