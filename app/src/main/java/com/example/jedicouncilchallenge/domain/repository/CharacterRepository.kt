package com.example.jedicouncilchallenge.domain.repository

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.FavouriteType
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.model.Species
import com.example.jedicouncilchallenge.domain.model.Starship
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CharacterRepository {
    suspend fun getCharacters(): Result<List<Character>, DataError.Network>
    suspend fun getSpecies(): Result<List<Species>, DataError.Network>
    suspend fun getPlanet(id: Int): Result<Planet, DataError.Network>
    suspend fun getStarship(id: Int): Result<Starship, DataError.Network>

    fun observeFavourites(): Flow<Set<FavouriteRef>>
    suspend fun toggleFavourite(ref: FavouriteRef)

    fun observeFavouriteCharacterIds(): Flow<Set<Int>> =
        observeFavourites().map { refs ->
            refs.filter { it.type == FavouriteType.CHARACTER }.map { it.id }.toSet()
        }

    fun observeDarthVaderMode(): Flow<Boolean>
    suspend fun setDarthVaderMode(enabled: Boolean)
}
