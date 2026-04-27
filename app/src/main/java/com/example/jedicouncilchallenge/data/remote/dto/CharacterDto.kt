package com.example.jedicouncilchallenge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val gender: String,
    @SerialName("birth_year") val birthYear: String,
    val height: String,
    val mass: String,
    @SerialName("hair_color") val hairColor: String,
    @SerialName("skin_color") val skinColor: String,
    @SerialName("eye_color") val eyeColor: String,
    val species: List<Int> = emptyList(),
    val films: List<Int> = emptyList(),
    val starships: List<Int> = emptyList(),
    val homeworld: Int? = null
)
