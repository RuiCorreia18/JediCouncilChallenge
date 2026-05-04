package com.example.jedicouncilchallenge.domain.usecase

import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Reads the current theme from DataStore and writes the negation. Reading inside the use case
 * (rather than passing the value in from a ViewModel) avoids a stale read against a
 * `WhileSubscribed`-backed StateFlow whose subscription may not yet have hydrated.
 */
class ToggleThemeUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke() {
        val current = repository.observeDarthVaderMode().first()
        repository.setDarthVaderMode(!current)
    }
}
