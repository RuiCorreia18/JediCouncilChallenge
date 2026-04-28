package com.example.jedicouncilchallenge.domain.model

data class CharacterDetail(
    val character: Character,
    val homeworld: Planet?,
    val starships: List<Starship>
)
