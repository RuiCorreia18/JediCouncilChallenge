package com.example.jedicouncilchallenge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val name: String,
    val gender: String,
    @SerialName("birth_year") val birthYear: String,
    val height: String,
    val mass: String,
    @SerialName("hair_color") val hairColor: String,
    @SerialName("skin_color") val skinColor: String,
    @SerialName("eye_color") val eyeColor: String,
    val species: List<String> = emptyList(),
    val films: List<String> = emptyList(),
    val starships: List<String> = emptyList(),
    val homeworld: String? = null,
    val url: String
)
