package com.example.jedicouncilchallenge.data.remote.datasource

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import com.example.jedicouncilchallenge.data.remote.dto.CharacterDto
import com.example.jedicouncilchallenge.data.remote.dto.PlanetDto
import com.example.jedicouncilchallenge.data.remote.dto.SpeciesDto
import com.example.jedicouncilchallenge.data.remote.dto.StarshipDto

interface RemoteDataSource {
    suspend fun getCharacters(): Result<List<CharacterDto>, DataError.Network>
    suspend fun getSpecies(): Result<List<SpeciesDto>, DataError.Network>
    suspend fun getPlanet(id: Int): Result<PlanetDto, DataError.Network>
    suspend fun getStarship(id: Int): Result<StarshipDto, DataError.Network>
}