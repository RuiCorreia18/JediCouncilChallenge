package com.example.jedicouncilchallenge.data.mapper

import com.example.jedicouncilchallenge.data.remote.dto.StarshipDto
import com.example.jedicouncilchallenge.domain.model.Starship

fun StarshipDto.toStarship(): Starship = Starship(
    id = url.resourceId(),
    name = name,
    model = model,
    manufacturer = manufacturer,
    starshipClass = starshipClass
)
