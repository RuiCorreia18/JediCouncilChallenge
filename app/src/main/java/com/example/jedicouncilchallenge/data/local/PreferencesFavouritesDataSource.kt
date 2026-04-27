package com.example.jedicouncilchallenge.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesFavouritesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FavouritesLocalDataSource {

    private object Keys {
        val FAVOURITE_IDS = stringSetPreferencesKey("favourite_ids")
    }

    override fun observeFavouriteIds(): Flow<Set<FavouriteId>> =
        dataStore.data.map { prefs ->
            prefs[Keys.FAVOURITE_IDS]
                ?.mapNotNull { it.toFavouriteIdOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    override suspend fun saveFavouriteId(id: FavouriteId) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.FAVOURITE_IDS] ?: emptySet()
            prefs[Keys.FAVOURITE_IDS] = current + id.encode()
        }
    }

    override suspend fun removeFavouriteId(id: FavouriteId) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.FAVOURITE_IDS] ?: emptySet()
            prefs[Keys.FAVOURITE_IDS] = current - id.encode()
        }
    }

    private fun FavouriteId.encode(): String = "${type.name}:$id"

    private fun String.toFavouriteIdOrNull(): FavouriteId? {
        val parts = split(":")
        if (parts.size != 2) return null
        val type = runCatching { FavouriteType.valueOf(parts[0]) }.getOrNull() ?: return null
        val id = parts[1].toIntOrNull() ?: return null
        return FavouriteId(type, id)
    }
}
