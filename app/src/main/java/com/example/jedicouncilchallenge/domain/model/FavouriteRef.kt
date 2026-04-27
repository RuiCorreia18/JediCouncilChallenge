package com.example.jedicouncilchallenge.domain.model

enum class FavouriteType { CHARACTER, SHIP, PLANET }

data class FavouriteRef(val id: Int, val type: FavouriteType)
