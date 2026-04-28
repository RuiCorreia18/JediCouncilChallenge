package com.example.jedicouncilchallenge.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpeciesDto(
    val name: String,
    val url: String
)
