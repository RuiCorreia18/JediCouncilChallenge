package com.example.jedicouncilchallenge.presentation.favourites

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

sealed interface FavouritesEvent {
    data class NavigateToDetail(val characterId: Int) : FavouritesEvent
}

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: CharacterRepository,
    private val getCharacters: GetCharactersUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavouritesState())
    val state = _state.asStateFlow()

    private val _events = Channel<FavouritesEvent>()
    val events = _events.receiveAsFlow()

    private var allCharacters: List<Character> = emptyList()
    private var favouriteCharacterIds: Set<Int> = emptySet()

    init {
        loadCharacters()
        observeFavourites()
    }

    fun retry() {
        loadCharacters()
    }

    fun onCharacterClick(characterId: Int) {
        viewModelScope.launch {
            _events.send(FavouritesEvent.NavigateToDetail(characterId))
        }
    }

    fun onRemoveFavourite(characterId: Int) {
        viewModelScope.launch {
            toggleFavourite(FavouriteRef(characterId, FavouriteType.CHARACTER))
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getCharacters()) {
                is Result.Success -> {
                    allCharacters = result.data.characters
                    _state.update { it.copy(isLoading = false) }
                    updateFavouriteCharacters()
                }
                is Result.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error.toUiText())
                }
            }
        }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.observeFavouriteCharacterIds().collect { ids ->
                favouriteCharacterIds = ids
                updateFavouriteCharacters()
            }
        }
    }

    private fun updateFavouriteCharacters() {
        _state.update {
            it.copy(
                characters = allCharacters
                    .filter { character -> character.id in favouriteCharacterIds }
                    .sortedBy { character -> character.name }
                    .map { character -> character.toFavouriteCharacterUi() }
            )
        }
    }

    private fun Character.toFavouriteCharacterUi() = FavouriteCharacterUi(
        id = id,
        name = name,
        imageUrl = characterImageUrl(id)
    )
}
