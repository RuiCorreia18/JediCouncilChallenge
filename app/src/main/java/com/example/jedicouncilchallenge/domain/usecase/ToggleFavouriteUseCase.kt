package com.example.jedicouncilchallenge.domain.usecase

import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(ref: FavouriteRef) = repository.toggleFavourite(ref)
}
