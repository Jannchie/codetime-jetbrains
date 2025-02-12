package dev.codetime

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile

import dev.codetime.utils.getGitBranchName
import dev.codetime.utils.getGitOriginUrl
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class CodetimeStartupActivity : ProjectActivity {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    override suspend fun execute(project: Project) {
        val projectName = project.name
        val projectPath = project.basePath
        if (projectPath == null) {
            println("Project path is null")
            return
        }
        val applicationInfo = ApplicationInfo.getInstance()
        val fullVersion = applicationInfo.fullVersion
        val fullApplicationName = applicationInfo.fullApplicationName
        val editor = fullApplicationName.substring(0, fullApplicationName.length - fullVersion.length)

        val platform = System.getProperty("os.name")
        val platformArch = System.getProperty("os.arch")
        val editorFactory = EditorFactory.getInstance()

        println("CodetimeStartupActivity: execute")

        suspend fun publishEventLog(file: VirtualFile, operationType: OperationType, eventType: EventType) {
            val absoluteFile = file.path
            val relativeFile = absoluteFile.substring(projectPath.length + 1)
            val fileType = file.fileType.name
            // Assume getGitOriginUrl and getGitBranchName might be blocking
            val gitOrigin = getGitOriginUrl(file)
            val gitBranch = getGitBranchName(file)
            val eventLog = EventLog(
                project = projectName,
                language = fileType,
                relativeFile = relativeFile,
                absoluteFile = absoluteFile,
                editor = editor,
                platform = platform,
                eventTime = System.currentTimeMillis(),
                eventType = eventType,
                platformArch = platformArch,
                plugin = "jetbrains",
                gitOrigin = gitOrigin,
                gitBranch = gitBranch,
                operationType = operationType
            )
            val json = Json.encodeToString(EventLog.serializer(), eventLog)
            val token = CodetimePropertiesUtils.getToken()
            val client = HttpClient(OkHttp) {
                defaultRequest {
                    header("token", token)
                }
            }
            client.post("https://api.codetime.dev/eventLog") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(json)
            }
            println("Event log published: $json")
        }

        editorFactory.eventMulticaster.addDocumentListener(object : DocumentListener {
            private var lastEventTime = 0L
            override fun documentChanged(event: DocumentEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEventTime < 5000) {
                    return
                }
                lastEventTime = currentTime
                coroutineScope.launch {
                    try {
                        val file = FileDocumentManager.getInstance().getFile(event.document) ?: return@launch
                        publishEventLog(file, OperationType.WRITE, EventType.FILE_EDITED)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }) {}

        editorFactory.eventMulticaster.addCaretListener(object : CaretListener {
            private var lastEventTime = 0L
            override fun caretPositionChanged(event: CaretEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEventTime < 5000) {
                    return
                }
                lastEventTime = currentTime
                coroutineScope.launch {
                    try {
                        val file = FileDocumentManager.getInstance().getFile(event.editor.document) ?: return@launch
                        publishEventLog(file, OperationType.READ, EventType.CHANGE_EDITOR_SELECTION)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }) {}

        editorFactory.eventMulticaster.addVisibleAreaListener(object : VisibleAreaListener {
            private var lastEventTime = 0L
            override fun visibleAreaChanged(event: VisibleAreaEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEventTime < 5000) {
                    return
                }
                lastEventTime = currentTime
                coroutineScope.launch {
                    try {
                        val file = FileDocumentManager.getInstance().getFile(event.editor.document) ?: return@launch
                        publishEventLog(file, OperationType.READ, EventType.CHANGE_EDITOR_VISIBLE_RANGES)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }) {}

        editorFactory.eventMulticaster.addSelectionListener(object : SelectionListener {
            private var lastEventTime = 0L
            override fun selectionChanged(event: SelectionEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEventTime < 5000) {
                    return
                }
                lastEventTime = currentTime
                coroutineScope.launch {
                    try {
                        val file = FileDocumentManager.getInstance().getFile(event.editor.document) ?: return@launch
                        publishEventLog(file, OperationType.READ, EventType.CHANGE_EDITOR_SELECTION)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }) {}

        editorFactory.addEditorFactoryListener(object : EditorFactoryListener {
            private var lastEventTime = 0L
            override fun editorCreated(event: EditorFactoryEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEventTime < 5000) {
                    return
                }
                lastEventTime = currentTime
                coroutineScope.launch {
                    try {
                        val file = FileDocumentManager.getInstance().getFile(event.editor.document) ?: return@launch
                        publishEventLog(file, OperationType.WRITE, EventType.FILE_CREATED)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }) {}

        editorFactory.eventMulticaster.addDocumentListener(object : DocumentListener {
            private var lastEventTime = 0L
            override fun documentChanged(event: DocumentEvent) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEventTime < 5000) {
                    return
                }
                lastEventTime = currentTime
                coroutineScope.launch {
                    try {
                        val file = FileDocumentManager.getInstance().getFile(event.document) ?: return@launch
                        publishEventLog(file, OperationType.READ, EventType.ACTIVATE_FILE_CHANGED)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }) {}
    }
}