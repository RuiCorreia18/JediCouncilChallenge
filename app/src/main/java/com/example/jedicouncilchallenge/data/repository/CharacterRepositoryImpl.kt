package com.example.jedicouncilchallenge.data.repository

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.core.domain.map
import com.example.jedicouncilchallenge.data.local.FavouritesLocalDataSource
import com.example.jedicouncilchallenge.data.local.ThemePreferences
import com.example.jedicouncilchallenge.data.mapper.toCharacter
import com.example.jedicouncilchallenge.data.mapper.toPlanet
import com.example.jedicouncilchallenge.data.mapper.toSpecies
import com.example.jedicouncilchallenge.data.mapper.toStarship
import com.example.jedicouncilchallenge.data.remote.datasource.RemoteDataSource
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Species
import com.example.jedicouncilchallenge.domain.model.Starship
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val favouritesLocalDataSource: FavouritesLocalDataSource,
    private val themePreferences: ThemePreferences
) : CharacterRepository {

    private val planetCache = mutableMapOf<Int, Planet>()
    private val starshipCache = mutableMapOf<Int, Starship>()
    private var characterCache: List<Character> = emptyList()

    override suspend fun getCharacters(): Result<List<Character>, DataError.Network> {
        if (characterCache.isNotEmpty()) return Result.Success(characterCache)
        return remoteDataSource.getCharacters().map { list -> list.map { it.toCharacter() } }
            .also { result ->
                if (result is Result.Success) characterCache = result.data
            }
    }

    override suspend fun getCharacter(id: Int): Result<Character, DataError.Network> {
        if (characterCache.isEmpty()) {
            when (val result = getCharacters()) {
                is Result.Success -> characterCache = result.data
                is Result.Error -> return result
            }
        }

        return characterCache.firstOrNull { it.id == id }
            ?.let { Result.Success(it) }
            ?: Result.Error(DataError.Network.NOT_FOUND)
    }

    override suspend fun getSpecies(): Result<List<Species>, DataError.Network> =
        remoteDataSource.getSpecies().map { list -> list.map { it.toSpecies() } }

    override suspend fun getPlanet(id: Int): Result<Planet, DataError.Network> {
        planetCache[id]?.let { return Result.Success(it) }
        return remoteDataSource.getPlanet(id).map { it.toPlanet() }.also { result ->
            if (result is Result.Success) planetCache[id] = result.data
        }
    }

    override suspend fun getStarship(id: Int): Result<Starship, DataError.Network> {
        starshipCache[id]?.let { return Result.Success(it) }
        return remoteDataSource.getStarship(id).map { it.toStarship() }.also { result ->
            if (result is Result.Success) starshipCache[id] = result.data
        }
    }

    override fun observeFavourites(): Flow<Set<FavouriteRef>> =
        favouritesLocalDataSource.observeFavourites()

    override suspend fun toggleFavourite(ref: FavouriteRef) {
        val current = favouritesLocalDataSource.observeFavourites().first()
        if (ref in current) favouritesLocalDataSource.remove(ref)
        else favouritesLocalDataSource.save(ref)
    }

    override fun observeDarthVaderMode(): Flow<Boolean> =
        themePreferences.observeDarthVaderMode()

    override suspend fun setDarthVaderMode(enabled: Boolean) =
        themePreferences.setDarthVaderMode(enabled)
}
