package com.example.jedicouncilchallenge.presentation.images

private const val CHARACTER_IMAGE_BASE_URL =
    "https://vieraboschkova.github.io/swapi-gallery/static/assets/img/people/"

fun characterImageUrl(characterId: Int): String =
    "$CHARACTER_IMAGE_BASE_URL$characterId.jpg"
