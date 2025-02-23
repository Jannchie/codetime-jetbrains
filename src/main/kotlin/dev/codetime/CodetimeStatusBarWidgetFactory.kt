package dev.codetime

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import com.intellij.openapi.wm.impl.status.TextPanel
import dev.codetime.utils.getDurationText
import dev.codetime.utils.getMinutes
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JPanel

class CodetimeStatusBarWidgetFactory : StatusBarWidgetFactory {


    override fun getId(): String {
        return "CodetimeStatusBarWidget"
    }

    override fun getDisplayName(): String {
        return "Code Time"
    }

    var normal = false

    override fun createWidget(project: Project): StatusBarWidget {
        return object : StatusBarWidget, EditorBasedStatusBarPopup(project, false) {

            private val timer = Timer()
            private val panel = TextPanel()

            init {

                CodetimePropertiesUtils.addListener { evt ->
                    if (evt != null) {
                        updatePanel()
                    }
                }
                panel.text = "Name"
                panel.toolTipText = "CodeTime"
                panel.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        if (e?.button == MouseEvent.BUTTON1) {
                            if (normal) {
                                val url = "https://codetime.dev"
                                java.awt.Desktop.getDesktop().browse(java.net.URI(url))
                            } else {
                                ShowSettingsUtil.getInstance().showSettingsDialog(project, "Codetime Settings")
                            }

                        }
                    }
                })

                timer.schedule(object : TimerTask() {
                    override fun run() {
                        updatePanel()
                    }
                }, 0, 60000)
                updatePanel()
            }

            fun updatePanel() {
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch {
                    val token = CodetimePropertiesUtils.getToken()
                    val timeRange = CodetimePropertiesUtils.getTimeRange()
                    var minutesParam = getMinutes(timeRange)
                    if (token.isBlank()) {
                        panel.text = "⏰ CodeTime: Click to enter your token"
                        normal = false
                    } else {
                        panel.text = "⏰ CodeTime: Loading..."
                        try {
                            val url = "https://api.codetime.dev/user/minutes?minutes=$minutesParam"
                            val client = HttpClient(OkHttp) {
                                defaultRequest {
                                    header("token", token)
                                }
                            }
                            val response: HttpResponse = client.get(url)
                            if (response.status.value == 200) {
                                val content = response.bodyAsText()
                                val minutesResponse =
                                    kotlinx.serialization.json.Json.decodeFromString<MinutesResponse>(content)
                                panel.text = "⏰ ${getDurationText(minutesResponse.minutes * 1000 * 60)}"
                                normal = true
                            } else {
                                panel.text = "⏰ Codetime: Invalid token"
                                normal = false
                            }
                        } catch (e: Exception) {
                            panel.text = "⏰ Codetime: Error"
                            normal = false
                            e.printStackTrace()
                        }
                    }
                    println(panel.text)
                    panel.revalidate()
                    panel.updateUI()
                }
            }

            override fun ID(): String {
                return "CodetimeStatusBarWidget"
            }

            override fun createInstance(project: Project): StatusBarWidget {
                return this
            }

            override fun createPopup(context: DataContext): ListPopup? {
                return null
            }

            override fun dispose() {
                timer.cancel()
            }

            override fun createComponent(): JPanel {
                return panel
            }


            override fun getWidgetState(file: VirtualFile?): WidgetState {
                return WidgetState.NO_CHANGE_MAKE_VISIBLE
            }
        }
    }
}