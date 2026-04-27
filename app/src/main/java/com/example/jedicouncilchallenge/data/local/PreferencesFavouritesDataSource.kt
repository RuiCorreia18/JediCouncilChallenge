package com.example.jedicouncilchallenge.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.FavouriteType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesFavouritesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FavouritesLocalDataSource {

    private object Keys {
        val FAVOURITES = stringSetPreferencesKey("favourite_ids")
    }

    override fun observeFavourites(): Flow<Set<FavouriteRef>> =
        dataStore.data.map { prefs ->
            prefs[Keys.FAVOURITES]
                ?.mapNotNull { it.toFavouriteRefOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    override suspend fun save(ref: FavouriteRef) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.FAVOURITES] ?: emptySet()
            prefs[Keys.FAVOURITES] = current + ref.encode()
        }
    }

    override suspend fun remove(ref: FavouriteRef) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.FAVOURITES] ?: emptySet()
            prefs[Keys.FAVOURITES] = current - ref.encode()
        }
    }

    private fun FavouriteRef.encode(): String = "${type.name}:$id"

    private fun String.toFavouriteRefOrNull(): FavouriteRef? {
        val parts = split(":")
        if (parts.size != 2) return null
        val type = runCatching { FavouriteType.valueOf(parts[0]) }.getOrNull() ?: return null
        val id = parts[1].toIntOrNull() ?: return null
        return FavouriteRef(id, type)
    }
}
