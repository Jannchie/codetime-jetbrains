package dev.codetime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OperationType {
    @SerialName("read")
    READ,

    @SerialName("write")
    WRITE
}