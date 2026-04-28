package com.example.jedicouncilchallenge.domain.usecase

import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeDarthVaderMode()
}
