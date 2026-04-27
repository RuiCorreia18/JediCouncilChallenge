package com.example.jedicouncilchallenge.data.local

import kotlinx.coroutines.flow.Flow

interface FavouritesLocalDataSource {
    fun observeFavouriteIds(): Flow<Set<FavouriteId>>
    suspend fun saveFavouriteId(id: FavouriteId)
    suspend fun removeFavouriteId(id: FavouriteId)
}
