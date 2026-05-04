package com.example.jedicouncilchallenge.domain.usecase

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.domain.model.CharacterDetail
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Starship
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Resolves a character's homeworld and starships in parallel, with early-exit on any network error.
 * The character itself is read from the in-memory list; only planets and starships require network calls.
 */
class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int): Result<CharacterDetail, DataError.Network> =
        coroutineScope {
            val character = when (val result = repository.getCharacter(characterId)) {
                is Result.Success -> result.data
                is Result.Error -> return@coroutineScope result
            }

            val homeworldDeferred = character.homeworldId?.let { id ->
                async { repository.getPlanet(id) }
            }
            val starshipDeferreds = character.starshipIds.map { id ->
                async { repository.getStarship(id) }
            }
            val detailDeferreds = listOfNotNull(homeworldDeferred) + starshipDeferreds

            val homeworld = when (val result = homeworldDeferred?.await()) {
                null -> null
                is Result.Success -> result.data
                is Result.Error -> {
                    detailDeferreds.forEach { it.cancel() }
                    return@coroutineScope result
                }
            }

            val starships = buildList {
                for (deferred in starshipDeferreds) {
                    when (val result = deferred.await()) {
                        is Result.Success -> add(result.data)
                        is Result.Error -> {
                            detailDeferreds.forEach { it.cancel() }
                            return@coroutineScope result
                        }
                    }
                }
            }

            Result.Success(
                CharacterDetail(
                    character = character,
                    homeworld = homeworld,
                    starships = starships
                )
            )
        }
}
