package com.example.jedicouncilchallenge.data.local

enum class FavouriteType { CHARACTER, SHIP, PLANET }

data class FavouriteId(val type: FavouriteType, val id: Int)
