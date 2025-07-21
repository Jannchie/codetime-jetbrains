package dev.codetime.utils

import java.time.ZoneId
import java.time.ZonedDateTime

fun getMinutes(key: String): Int {
    var minutes = 60 * 24
    when (key) {
        "Today code time" -> {
            val zoneId = ZoneId.systemDefault()
            val now = ZonedDateTime.now(zoneId)
            val hours = now.hour
            minutes = now.minute + hours * 60
        }

        "Total code time" -> {
            minutes = 60 * 24 * 365 * 100
        }

        "24h code time" -> {
            minutes = 60 * 24
        }

        else -> {
            minutes = 60 * 24 * 365 * 100
        }
    }
    return minutes
}