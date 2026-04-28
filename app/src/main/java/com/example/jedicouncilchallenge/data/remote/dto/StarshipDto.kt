package com.example.jedicouncilchallenge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StarshipDto(
    val name: String,
    val model: String,
    val manufacturer: String,
    @SerialName("starship_class") val starshipClass: String,
    val url: String
)
