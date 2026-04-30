package com.example.jedicouncilchallenge.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.core.presentation.toUiText
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.FavouriteType
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import com.example.jedicouncilchallenge.domain.usecase.GetCharactersUseCase
import com.example.jedicouncilchallenge.domain.usecase.ToggleFavouriteUseCase
import com.example.jedicouncilchallenge.presentation.images.characterImageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CharacterListEvent {
    data class NavigateToDetail(val characterId: Int) : CharacterListEvent
}

private const val PAGE_SIZE = 20

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharacters: GetCharactersUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val repository: CharacterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterListState())
    val state = _state.asStateFlow()

    private val _events = Channel<CharacterListEvent>()
    val events = _events.receiveAsFlow()

    private var allCharacters: List<Character> = emptyList()
    private var speciesMap: Map<Int, String> = emptyMap()
    private var visibleCount = PAGE_SIZE

    init {
        loadInitialData()
        observeFavourites()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getCharacters()) {
                is Result.Success -> {
                    allCharacters = result.data.characters
                    speciesMap = result.data.species.associate { it.id to it.name }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            availableSpecies = result.data.species
                                .map { s -> s.name }.distinct().sorted(),
                            availableGenders = allCharacters
                                .map { c -> c.gender }.distinct().sorted()
                        )
                    }
                    updateDisplayedCharacters()
                }
                is Result.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error.toUiText())
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        visibleCount = PAGE_SIZE // reset pagination — a new filter set starts from page 1
        _state.update { it.copy(searchQuery = query) }
        updateDisplayedCharacters()
    }

    fun onSpeciesFilterChange(species: String?) {
        visibleCount = PAGE_SIZE
        _state.update { it.copy(selectedSpecies = species) }
        updateDisplayedCharacters()
    }

    fun onGenderFilterChange(gender: String?) {
        visibleCount = PAGE_SIZE
        _state.update { it.copy(selectedGender = gender) }
        updateDisplayedCharacters()
    }

    fun onSortOptionChange(sortOption: CharacterSortOption) {
        visibleCount = PAGE_SIZE
        _state.update { it.copy(sortOption = sortOption) }
        updateDisplayedCharacters()
    }

    fun loadMore() {
        visibleCount += PAGE_SIZE
        updateDisplayedCharacters()
    }

    fun onCharacterClick(id: Int) {
        viewModelScope.launch {
            _events.send(CharacterListEvent.NavigateToDetail(id))
        }
    }

    fun onToggleFavourite(id: Int) {
        viewModelScope.launch {
            toggleFavourite(FavouriteRef(id, FavouriteType.CHARACTER))
        }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.observeFavouriteCharacterIds().collect { ids ->
                _state.update { it.copy(favouriteCharacterIds = ids) }
            }
        }
    }

    private fun updateDisplayedCharacters() {
        val query = _state.value.searchQuery
        val species = _state.value.selectedSpecies
        val gender = _state.value.selectedGender
        val sortOption = _state.value.sortOption

        val filtered = allCharacters.filter { character ->
            val matchesSearch = query.isBlank() ||
                character.name.contains(query, ignoreCase = true)
            val matchesSpecies = species == null ||
                character.speciesIds.any { speciesMap[it] == species }
            val matchesGender = gender == null ||
                character.gender.equals(gender, ignoreCase = true)
            matchesSearch && matchesSpecies && matchesGender
        }.let { characters ->
            when (sortOption) {
                CharacterSortOption.NameAscending -> characters.sortedBy { it.name }
                CharacterSortOption.NameDescending -> characters.sortedByDescending { it.name }
                CharacterSortOption.BirthYearAscending -> characters.sortedWith(
                    compareBy<Character> { it.birthYear.timelineYear() ?: Double.POSITIVE_INFINITY }
                        .thenBy { it.name }
                )

                CharacterSortOption.BirthYearDescending -> characters.sortedWith(
                    compareByDescending<Character> {
                        it.birthYear.timelineYear() ?: Double.NEGATIVE_INFINITY
                    }
                        .thenBy { it.name }
                )
            }
        }

        _state.update {
            it.copy(
                displayedCharacters = filtered.take(visibleCount)
                    .map { c -> c.toCharacterUi(speciesMap) },
                canLoadMore = filtered.size > visibleCount
            )
        }
    }

    private fun Character.toCharacterUi(speciesMap: Map<Int, String>) = CharacterUi(
        id = id,
        name = name,
        imageUrl = characterImageUrl(id),
        speciesName = speciesIds.firstOrNull()?.let { speciesMap[it] } ?: "Unknown"
    )

    private fun String.timelineYear(): Double? {
        val normalized = trim().uppercase()
        val year = normalized
            .removeSuffix("BBY")
            .removeSuffix("ABY")
            .toDoubleOrNull() ?: return null

        return when {
            normalized.endsWith("BBY") -> -year
            normalized.endsWith("ABY") -> year
            else -> null
        }
    }
}
