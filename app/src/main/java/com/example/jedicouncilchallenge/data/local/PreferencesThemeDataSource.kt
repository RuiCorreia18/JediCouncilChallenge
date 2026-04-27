package com.example.jedicouncilchallenge.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesThemeDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemePreferences {

    private object Keys {
        val DARTH_VADER_MODE = booleanPreferencesKey("darth_vader_mode")
    }

    override fun observeDarthVaderMode(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[Keys.DARTH_VADER_MODE] ?: false }

    override suspend fun setDarthVaderMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.DARTH_VADER_MODE] = enabled }
    }
}
