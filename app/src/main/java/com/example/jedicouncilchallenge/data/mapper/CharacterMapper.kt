package com.example.jedicouncilchallenge.data.mapper

import com.example.jedicouncilchallenge.data.remote.dto.CharacterDto
import com.example.jedicouncilchallenge.domain.model.Character

fun CharacterDto.toCharacter(): Character = Character(
    id = url.resourceId(),
    name = name,
    gender = gender,
    birthYear = birthYear,
    height = height,
    mass = mass,
    hairColor = hairColor,
    skinColor = skinColor,
    eyeColor = eyeColor,
    speciesIds = species.map { it.resourceId() },
    filmIds = films.map { it.resourceId() },
    starshipIds = starships.map { it.resourceId() },
    homeworldId = homeworld.resourceIdOrNull()
)
