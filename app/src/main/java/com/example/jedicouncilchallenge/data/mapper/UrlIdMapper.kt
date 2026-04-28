package com.example.jedicouncilchallenge.data.mapper

private val ResourceIdRegex = """/(\d+)\.json$""".toRegex()

fun String.resourceId(): Int =
    ResourceIdRegex.find(this)?.groupValues?.get(1)?.toIntOrNull() ?: 0

fun String?.resourceIdOrNull(): Int? =
    this?.resourceId()?.takeIf { it != 0 }
