package com.example.jedicouncilchallenge.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.core.presentation.toUiText
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.FavouriteType
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import com.example.jedicouncilchallenge.domain.usecase.GetCharacterDetailUseCase
import com.example.jedicouncilchallenge.domain.usecase.ToggleFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetail: GetCharacterDetailUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val repository: CharacterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    private var currentCharacterId: Int? = null
    private var favouriteJob: Job? = null

    fun loadCharacter(characterId: Int) {
        if (currentCharacterId == characterId && _state.value.character != null) return
        currentCharacterId = characterId

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
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
        currentCharacterId?.let(::loadCharacter)
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
            repository.observeFavouriteCharacterIds().collect { ids ->
                _state.update { it.copy(isFavourite = characterId in ids) }
            }
        }
    }
}
