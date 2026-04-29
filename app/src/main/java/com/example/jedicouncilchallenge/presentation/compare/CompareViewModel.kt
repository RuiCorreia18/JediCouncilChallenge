package com.example.jedicouncilchallenge.presentation.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.core.presentation.toUiText
import com.example.jedicouncilchallenge.core.presentation.UiText
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import com.example.jedicouncilchallenge.presentation.images.characterImageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompareState(isLoading = true))
    val state = _state.asStateFlow()

    private var characters: List<CompareCharacterUi> = emptyList()

    init {
        loadCharacters()
    }

    fun retry() { loadCharacters() }

    fun compare() { _state.update { it.copy(isComparing = true) } }

    fun backFromCompare() { _state.update { it.copy(isComparing = false) } }

    fun openLeftPicker() {
        _state.update {
            it.copy(
                isLeftPickerOpen = true,
                leftQuery = "",
                leftSuggestions = suggestionsFor("", excludedId = it.rightSelected?.id)
            )
        }
    }

    fun openRightPicker() {
        _state.update {
            it.copy(
                isRightPickerOpen = true,
                rightQuery = "",
                rightSuggestions = suggestionsFor("", excludedId = it.leftSelected?.id)
            )
        }
    }

    fun closeLeftPicker() {
        _state.update { it.copy(isLeftPickerOpen = false, leftQuery = "", leftSuggestions = emptyList()) }
    }

    fun closeRightPicker() {
        _state.update { it.copy(isRightPickerOpen = false, rightQuery = "", rightSuggestions = emptyList()) }
    }

    fun onLeftQueryChange(query: String) {
        _state.update {
            it.copy(
                leftQuery = query,
                leftSuggestions = suggestionsFor(query, excludedId = it.rightSelected?.id)
            )
        }
    }

    fun onRightQueryChange(query: String) {
        _state.update {
            it.copy(
                rightQuery = query,
                rightSuggestions = suggestionsFor(query, excludedId = it.leftSelected?.id)
            )
        }
    }

    fun selectLeft(character: CompareCharacterUi) {
        _state.update {
            it.copy(
                leftSelected = character,
                leftQuery = "",
                leftSuggestions = emptyList(),
                isLeftPickerOpen = false,
                rightSuggestions = suggestionsFor(it.rightQuery, excludedId = character.id)
            )
        }
    }

    fun selectRight(character: CompareCharacterUi) {
        _state.update {
            it.copy(
                rightSelected = character,
                rightQuery = "",
                rightSuggestions = emptyList(),
                isRightPickerOpen = false,
                leftSuggestions = suggestionsFor(it.leftQuery, excludedId = character.id)
            )
        }
    }

    fun clearLeft() {
        _state.update {
            it.copy(leftSelected = null, leftQuery = "", leftSuggestions = emptyList(), isComparing = false)
        }
    }

    fun clearRight() {
        _state.update {
            it.copy(rightSelected = null, rightQuery = "", rightSuggestions = emptyList(), isComparing = false)
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val (chars, speciesList) = coroutineScope {
                    val charsDeferred = async { repository.getCharacters() }
                    val speciesDeferred = async { repository.getSpecies() }
                    charsDeferred.await() to speciesDeferred.await()
                }
                if (chars is Result.Error) {
                    _state.update { it.copy(isLoading = false, error = chars.error.toUiText()) }
                    return@launch
                }
                val speciesMap: Map<Int, String> = when (speciesList) {
                    is Result.Success -> speciesList.data.associate { it.id to it.name }
                    is Result.Error -> emptyMap()
                }
                characters = (chars as Result.Success).data.map { it.toCompareCharacterUi(speciesMap) }
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = UiText.DynamicString(e.message ?: "Unknown error")) }
            }
        }
    }

    private fun suggestionsFor(query: String, excludedId: Int?): List<CompareCharacterUi> =
        characters
            .asSequence()
            .filter { it.id != excludedId }
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
            .sortedBy { it.name }
            .toList()

    private fun Character.toCompareCharacterUi(speciesMap: Map<Int, String>) = CompareCharacterUi(
        id = id,
        name = name,
        imageUrl = characterImageUrl(id),
        speciesName = speciesIds.firstOrNull()?.let { speciesMap[it] } ?: "Human",
        gender = gender.displayValue(),
        birthYear = birthYear.displayValue(),
        height = height.displayValue(),
        mass = mass.displayValue(),
        hairColor = hairColor.displayValue(),
        eyeColor = eyeColor.displayValue(),
        starshipCount = starshipIds.size,
        filmCount = filmIds.size
    )
}

private fun String.displayValue(): String =
    if (equals("unknown", ignoreCase = true)) "Unknown" else replaceFirstChar { it.uppercase() }
