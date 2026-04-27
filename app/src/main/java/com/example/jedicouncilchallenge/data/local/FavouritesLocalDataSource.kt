package com.example.jedicouncilchallenge.data.local

import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import kotlinx.coroutines.flow.Flow

interface FavouritesLocalDataSource {
    fun observeFavourites(): Flow<Set<FavouriteRef>>
    suspend fun save(ref: FavouriteRef)
    suspend fun remove(ref: FavouriteRef)
}
