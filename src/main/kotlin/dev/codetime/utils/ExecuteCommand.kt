package dev.codetime.utils

import java.io.BufferedReader
import java.io.InputStreamReader


fun executeCommand(processBuilder: ProcessBuilder): String? {
    return try {
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        reader.lines().forEach { line -> output.append(line).append("\n") }
        process.waitFor()
        reader.close()
        output.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}