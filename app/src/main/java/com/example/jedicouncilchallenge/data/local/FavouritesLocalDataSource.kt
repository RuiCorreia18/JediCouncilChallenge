package com.example.jedicouncilchallenge.data.local

import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import kotlinx.coroutines.flow.Flow

/** Persists and observes the set of favourited items across app sessions. */
interface FavouritesLocalDataSource {
    fun observeFavourites(): Flow<Set<FavouriteRef>>
    suspend fun save(ref: FavouriteRef)
    suspend fun remove(ref: FavouriteRef)
}
