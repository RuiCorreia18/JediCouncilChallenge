package com.example.jedicouncilchallenge.domain.model

data class Character(
    val id: Int,
    val name: String,
    val gender: String,
    val birthYear: String,
    val height: String,
    val mass: String,
    val hairColor: String,
    val skinColor: String,
    val eyeColor: String,
    val speciesIds: List<Int>,
    val filmIds: List<Int>,
    val starshipIds: List<Int>,
    val homeworldId: Int?
)
