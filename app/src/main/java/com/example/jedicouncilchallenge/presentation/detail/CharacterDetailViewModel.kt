package com.example.jedicouncilchallenge.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.core.presentation.toUiText
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.FavouriteType
import com.example.jedicouncilchallenge.domain.usecase.GetCharacterDetailUseCase
import com.example.jedicouncilchallenge.domain.usecase.GetFavouritesUseCase
import com.example.jedicouncilchallenge.domain.usecase.ToggleFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetail: GetCharacterDetailUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val getFavourites: GetFavouritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    private var currentCharacterId: Int? = null
    private var favouriteJob: Job? = null
    private var loadJob: Job? = null

    fun loadCharacter(characterId: Int) = loadCharacter(characterId, forceReload = false)

    private fun loadCharacter(characterId: Int, forceReload: Boolean) {
        if (!forceReload && currentCharacterId == characterId && _state.value.character?.id == characterId) return
        currentCharacterId = characterId

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    character = null,
                    homeworld = null,
                    starships = emptyList(),
                    isLoading = true,
                    error = null
                )
            }
            when (val result = getCharacterDetail(characterId)) {
                is Result.Success -> _state.update {
                    it.copy(
                        character = result.data.character,
                        homeworld = result.data.homeworld,
                        starships = result.data.starships,
                        isLoading = false
                    )
                }
                is Result.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error.toUiText())
                }
            }
        }

        observeFavourite(characterId)
    }

    fun retry() {
        currentCharacterId?.let { loadCharacter(it, forceReload = true) }
    }

    fun toggleFavourite() {
        val characterId = currentCharacterId ?: return
        viewModelScope.launch {
            toggleFavourite(FavouriteRef(characterId, FavouriteType.CHARACTER))
        }
    }

    private fun observeFavourite(characterId: Int) {
        favouriteJob?.cancel()
        favouriteJob = viewModelScope.launch {
            getFavourites().collect { refs ->
                _state.update { it.copy(isFavourite = refs.hasCharacter(characterId)) }
            }
        }
    }

    private fun Set<FavouriteRef>.hasCharacter(characterId: Int): Boolean =
        any { it.type == FavouriteType.CHARACTER && it.id == characterId }
}
