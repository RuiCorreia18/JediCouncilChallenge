package com.example.jedicouncilchallenge.domain.usecase

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.domain.model.CharactersWithSpecies
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(): Result<CharactersWithSpecies, DataError.Network> =
        coroutineScope {
            val charactersDeferred = async { repository.getCharacters() }
            val speciesDeferred = async { repository.getSpecies() }

            val characters = charactersDeferred.await()
            val species = speciesDeferred.await()

            if (characters is Result.Error) return@coroutineScope characters
            if (species is Result.Error) return@coroutineScope species

            Result.Success(
                CharactersWithSpecies(
                    characters = (characters as Result.Success).data,
                    species = (species as Result.Success).data
                )
            )
        }
}
