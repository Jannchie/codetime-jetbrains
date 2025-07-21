package dev.codetime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EventType {
    @SerialName("activateFileChanged")
    ACTIVATE_FILE_CHANGED,

    @SerialName("editorChanged")
    EDITOR_CHANGED,

    @SerialName("fileAddedLine")
    FILE_ADDED_LINE,

    @SerialName("fileCreated")
    FILE_CREATED,

    @SerialName("fileEdited")
    FILE_EDITED,

    @SerialName("fileRemoved")
    FILE_REMOVED,

    @SerialName("fileSaved")
    FILE_SAVED,

    @SerialName("changeEditorSelection")
    CHANGE_EDITOR_SELECTION,

    @SerialName("changeEditorVisibleRanges")
    CHANGE_EDITOR_VISIBLE_RANGES
}