package dev.codetime.utils

const val MS_OF_HOUR = 3600000
const val MS_OF_MINUTE = 60000

fun getDurationText(ms: Long): String {
    var remainingMs = ms
    var result = ""

    if (remainingMs > MS_OF_HOUR) {
        if (result.isNotEmpty()) {
            result += " "
        }

        val hours = remainingMs / MS_OF_HOUR
        result += "${hours}hr"
        if (hours > 1) {
            result += "s"
        }

        remainingMs %= MS_OF_HOUR
    }
    if (remainingMs > MS_OF_MINUTE) {
        if (result.isNotEmpty()) {
            result += " "
        }

        val minutes = remainingMs / MS_OF_MINUTE
        result += "${minutes}min"
        if (minutes > 1) {
            result += "s"
        }

        remainingMs %= MS_OF_MINUTE
    }
    if (result.isNotEmpty()) {
        return result
    }

    val seconds = remainingMs / 1000
    result += "${seconds}sec"
    if (seconds > 1) {
        result += "s"
    }

    return result
}