package com.example.jedicouncilchallenge.data.remote

import com.example.jedicouncilchallenge.data.remote.dto.CharacterDto
import com.example.jedicouncilchallenge.data.remote.dto.PlanetDto
import com.example.jedicouncilchallenge.data.remote.dto.SpeciesDto
import com.example.jedicouncilchallenge.data.remote.dto.StarshipDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SwapiApiService {

    @GET("api/people/all.json")
    suspend fun getAllCharacters(): List<CharacterDto>

    @GET("api/species/all.json")
    suspend fun getAllSpecies(): List<SpeciesDto>

    @GET("api/planets/{id}.json")
    suspend fun getPlanet(@Path("id") id: Int): PlanetDto

    @GET("api/starships/{id}.json")
    suspend fun getStarship(@Path("id") id: Int): StarshipDto
}
