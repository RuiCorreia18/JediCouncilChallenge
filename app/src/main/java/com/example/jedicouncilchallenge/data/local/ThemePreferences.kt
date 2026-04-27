package com.example.jedicouncilchallenge.data.local

import kotlinx.coroutines.flow.Flow

interface ThemePreferences {
    fun observeDarthVaderMode(): Flow<Boolean>
    suspend fun setDarthVaderMode(enabled: Boolean)
}
