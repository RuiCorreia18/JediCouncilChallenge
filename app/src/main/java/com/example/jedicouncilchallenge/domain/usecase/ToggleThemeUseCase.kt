package com.example.jedicouncilchallenge.domain.usecase

import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import javax.inject.Inject

class ToggleThemeUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(currentlyEnabled: Boolean) {
        repository.setDarthVaderMode(!currentlyEnabled)
    }
}
