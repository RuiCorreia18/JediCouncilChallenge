package com.example.jedicouncilchallenge.domain.repository

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Species
import com.example.jedicouncilchallenge.domain.model.Starship
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for all Star Wars data.
 *
 * Characters are fetched once from the network and held in memory; [getCharacter] reads from that
 * in-memory list rather than making a redundant network call. Planets and starships are lazy-fetched
 * on detail open and cached at the implementation level to avoid re-fetching shared homeworlds.
 */
interface CharacterRepository {
    /** Fetches the full character list from the network. Call once on startup. */
    suspend fun getCharacters(): Result<List<Character>, DataError.Network>

    /** Returns a character from the in-memory list populated by [getCharacters]. */
    suspend fun getCharacter(id: Int): Result<Character, DataError.Network>

    suspend fun getSpecies(): Result<List<Species>, DataError.Network>

    /** Lazy-fetches a planet; the implementation caches results for the session. */
    suspend fun getPlanet(id: Int): Result<Planet, DataError.Network>

    /** Lazy-fetches a starship; the implementation caches results for the session. */
    suspend fun getStarship(id: Int): Result<Starship, DataError.Network>

    fun observeFavourites(): Flow<Set<FavouriteRef>>
    suspend fun toggleFavourite(ref: FavouriteRef)

    fun observeDarthVaderMode(): Flow<Boolean>
    suspend fun setDarthVaderMode(enabled: Boolean)
}
