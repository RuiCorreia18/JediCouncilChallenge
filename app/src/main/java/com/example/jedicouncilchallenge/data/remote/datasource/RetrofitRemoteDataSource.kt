package com.example.jedicouncilchallenge.data.remote.datasource

import com.example.jedicouncilchallenge.core.data.safeCall
import com.example.jedicouncilchallenge.data.remote.SwapiApiService
import javax.inject.Inject

class RetrofitRemoteDataSource @Inject constructor(
    private val service: SwapiApiService
) : RemoteDataSource {

    override suspend fun getCharacters() = safeCall { service.getAllCharacters() }
    override suspend fun getSpecies() = safeCall { service.getAllSpecies() }
    override suspend fun getPlanet(id: Int) = safeCall { service.getPlanet(id) }
    override suspend fun getStarship(id: Int) = safeCall { service.getStarship(id) }
}