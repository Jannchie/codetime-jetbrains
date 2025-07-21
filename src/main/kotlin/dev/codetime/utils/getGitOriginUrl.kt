package dev.codetime.utils

import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun getGitOriginUrl( file: VirtualFile): String? {
    val filePath = File(file.path).parentFile ?: return null
    val processBuilder = ProcessBuilder("git", "remote", "get-url", "origin")
    processBuilder.directory(filePath)
    return executeCommand(processBuilder)?.trim()
}