package com.example.jedicouncilchallenge.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlanetDto(
    val id: Int,
    val name: String,
    val climate: String,
    val terrain: String,
    val population: String
)
