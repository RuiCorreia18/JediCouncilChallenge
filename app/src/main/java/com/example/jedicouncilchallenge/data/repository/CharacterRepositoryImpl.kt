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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val favouritesLocalDataSource: FavouritesLocalDataSource,
    private val themePreferences: ThemePreferences
) : CharacterRepository {

    // The repository is bound as @Singleton, so these caches are shared across coroutines.
    // The mutex serialises cache reads and writes — without it, concurrent callers (e.g. list
    // init + a deeplinked detail) could race and corrupt the maps or trigger duplicate fetches.
    private val cacheMutex = Mutex()
    private val planetCache = mutableMapOf<Int, Planet>()
    private val starshipCache = mutableMapOf<Int, Starship>()
    private var characterCache: List<Character> = emptyList()

    override suspend fun getCharacters(): Result<List<Character>, DataError.Network> =
        // Hold the lock across the network call: the list is fetched once on startup, so
        // serialising the first concurrent callers is preferable to two parallel fetches of
        // the full payload. After the cache fills, every subsequent call short-circuits.
        cacheMutex.withLock {
            if (characterCache.isNotEmpty()) return@withLock Result.Success(characterCache)
            remoteDataSource.getCharacters()
                .map { list -> list.map { it.toCharacter() } }
                .also { result ->
                    if (result is Result.Success) characterCache = result.data
                }
        }

    override suspend fun getCharacter(id: Int): Result<Character, DataError.Network> {
        // The list endpoint returns the same per-character shape as the per-id endpoint,
        // so we populate the cache via getCharacters() rather than making a redundant per-id call.
        if (cacheMutex.withLock { characterCache.isEmpty() }) {
            when (val result = getCharacters()) {
                is Result.Success -> Unit // cache populated atomically inside getCharacters()
                is Result.Error -> return result
            }
        }

        return cacheMutex.withLock { characterCache.firstOrNull { it.id == id } }
            ?.let { Result.Success(it) }
            ?: Result.Error(DataError.Network.NOT_FOUND)
    }

    override suspend fun getSpecies(): Result<List<Species>, DataError.Network> =
        remoteDataSource.getSpecies().map { list -> list.map { it.toSpecies() } }

    override suspend fun getPlanet(id: Int): Result<Planet, DataError.Network> {
        // Lock only for cache read and write — the network fetch runs outside the lock so
        // parallel detail fetches for different planets/starships don't serialise on each other.
        cacheMutex.withLock { planetCache[id] }?.let { return Result.Success(it) }
        return remoteDataSource.getPlanet(id).map { it.toPlanet() }.also { result ->
            if (result is Result.Success) {
                cacheMutex.withLock { planetCache[id] = result.data }
            }
        }
    }

    override suspend fun getStarship(id: Int): Result<Starship, DataError.Network> {
        cacheMutex.withLock { starshipCache[id] }?.let { return Result.Success(it) }
        return remoteDataSource.getStarship(id).map { it.toStarship() }.also { result ->
            if (result is Result.Success) {
                cacheMutex.withLock { starshipCache[id] = result.data }
            }
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
