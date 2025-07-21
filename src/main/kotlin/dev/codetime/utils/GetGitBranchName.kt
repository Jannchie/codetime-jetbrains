package dev.codetime.utils

import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun getGitBranchName(file: VirtualFile): String? {
    val filePath = File(file.path).parentFile ?: return null
    val processBuilder = ProcessBuilder("git", "branch", "--show-current")
    processBuilder.directory(filePath)
    return executeCommand(processBuilder)?.trim()
}