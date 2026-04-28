package com.example.jedicouncilchallenge.fake

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Species
import com.example.jedicouncilchallenge.domain.model.Starship
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

open class FakeCharacterRepository(
    private val characters: List<Character> = emptyList(),
    private val species: List<Species> = emptyList(),
    private val planets: List<Planet> = emptyList(),
    private val starships: List<Starship> = emptyList(),
    // When true, getCharacters() suspends until releaseCharacters() is called.
    // Use this to observe the loading state before data arrives.
    val blockCharacters: Boolean = false,
    private val networkError: DataError.Network? = null
) : CharacterRepository {

    private val _favourites = MutableStateFlow<Set<FavouriteRef>>(emptySet())
    private val gate = MutableStateFlow(false)

    fun releaseCharacters() { gate.value = true }

    override suspend fun getCharacters(): Result<List<Character>, DataError.Network> {
        networkError?.let { return Result.Error(it) }
        if (blockCharacters) gate.first { it }
        return Result.Success(characters)
    }

    override suspend fun getSpecies(): Result<List<Species>, DataError.Network> =
        networkError?.let { Result.Error(it) } ?: Result.Success(species)

    override suspend fun getCharacter(id: Int): Result<Character, DataError.Network> {
        networkError?.let { return Result.Error(it) }
        return characters.firstOrNull { it.id == id }
            ?.let { Result.Success(it) }
            ?: Result.Error(DataError.Network.NOT_FOUND)
    }

    override suspend fun getPlanet(id: Int): Result<Planet, DataError.Network> =
        networkError?.let { Result.Error(it) }
            ?: planets.firstOrNull { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(DataError.Network.NOT_FOUND)

    override suspend fun getStarship(id: Int): Result<Starship, DataError.Network> =
        networkError?.let { Result.Error(it) }
            ?: starships.firstOrNull { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(DataError.Network.NOT_FOUND)

    override fun observeFavourites(): Flow<Set<FavouriteRef>> = _favourites.asStateFlow()

    override suspend fun toggleFavourite(ref: FavouriteRef) {
        _favourites.update { if (ref in it) it - ref else it + ref }
    }

    override fun observeDarthVaderMode(): Flow<Boolean> = flowOf(false)

    override suspend fun setDarthVaderMode(enabled: Boolean) = Unit
}
