package com.example.jedicouncilchallenge.data.mapper

import com.example.jedicouncilchallenge.data.remote.dto.CharacterDto
import com.example.jedicouncilchallenge.domain.model.Character

fun CharacterDto.toCharacter(): Character = Character(
    id = id,
    name = name,
    gender = gender,
    birthYear = birthYear,
    height = height,
    mass = mass,
    hairColor = hairColor,
    skinColor = skinColor,
    eyeColor = eyeColor,
    speciesIds = species,
    filmIds = films,
    starshipIds = starships,
    homeworldId = homeworld
)
