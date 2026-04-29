package com.example.jedicouncilchallenge.data.mapper

// SWAPI encodes resource identity as the last path segment: /api/people/1.json
private val ResourceIdRegex = """/(\d+)\.json$""".toRegex()

// Returns 0 on parse failure — callers that need to distinguish "missing" from "unknown" use resourceIdOrNull().
fun String.resourceId(): Int =
    ResourceIdRegex.find(this)?.groupValues?.get(1)?.toIntOrNull() ?: 0

fun String?.resourceIdOrNull(): Int? =
    this?.resourceId()?.takeIf { it != 0 }
