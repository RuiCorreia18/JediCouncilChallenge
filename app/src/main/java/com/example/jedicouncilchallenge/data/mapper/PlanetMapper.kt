package com.example.jedicouncilchallenge.data.mapper

import com.example.jedicouncilchallenge.data.remote.dto.PlanetDto
import com.example.jedicouncilchallenge.domain.model.Planet

fun PlanetDto.toPlanet(): Planet = Planet(
    id = url.resourceId(),
    name = name,
    climate = climate,
    terrain = terrain,
    population = population
)
