package dev.codetime

import kotlinx.serialization.Serializable


@Serializable
data class EventLog(
    val project: String,
    val language: String,
    val relativeFile: String,
    val absoluteFile: String,
    val editor: String,
    val platform: String,
    val eventTime: Long,
    val platformArch: String,
    val plugin: String,
    val gitOrigin: String?,
    val gitBranch: String?,
    val eventType: EventType,
    val operationType: OperationType
)