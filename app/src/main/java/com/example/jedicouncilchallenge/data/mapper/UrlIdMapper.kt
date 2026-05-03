package com.example.jedicouncilchallenge.data.mapper

// SWAPI encodes resource identity as the last path segment: /api/people/1.json
private val ResourceIdRegex = """/(\d+)\.json$""".toRegex()

// Required resource IDs fail fast; optional relationships can use resourceIdOrNull().
fun String.resourceId(): Int =
    resourceIdOrNull() ?: error("Malformed SWAPI resource URL: $this")

fun String?.resourceIdOrNull(): Int? =
    this?.let { ResourceIdRegex.find(it)?.groupValues?.get(1)?.toIntOrNull() }
