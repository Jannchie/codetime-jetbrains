package dev.codetime

import kotlinx.serialization.Serializable

@Serializable
data class MinutesResponse(
    val minutes: Long
)