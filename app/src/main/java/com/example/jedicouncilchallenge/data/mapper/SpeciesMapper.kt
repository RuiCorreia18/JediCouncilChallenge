package com.example.jedicouncilchallenge.data.mapper

import com.example.jedicouncilchallenge.data.remote.dto.SpeciesDto
import com.example.jedicouncilchallenge.domain.model.Species

fun SpeciesDto.toSpecies(): Species = Species(
    id = url.resourceId(),
    name = name
)
