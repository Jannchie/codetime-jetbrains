package dev.codetime

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import io.ktor.util.*
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*
import kotlin.text.toCharArray

class CodetimeConfigurable : Configurable {
    private lateinit var mainPanel: JPanel
    private lateinit var tokenField: JPasswordField
    private lateinit var infoComboBox: JComboBox<String>

    override fun createComponent(): JComponent {
        val token = CodetimePropertiesUtils.getToken()
        // Main panel with border layout to keep everything at the top
        mainPanel = JPanel(BorderLayout())

        // A panel with BoxLayout for vertically stacking sections
        val contentPanel = JPanel()
        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)

        // Add some padding at the top
        contentPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        // Token Section
        val tokenPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val tokenLabel = JLabel("Token:")
        tokenField = JPasswordField(20)
        tokenField.text = token
        tokenPanel.add(tokenLabel)
        tokenPanel.add(tokenField)
        contentPanel.add(tokenPanel)

        // Info Section
        val timeRangePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val timeRangeLabel = JLabel("Info Time Range:")
        infoComboBox = ComboBox(arrayOf("Total code time", "24h code time", "Today code time"))

        timeRangePanel.add(timeRangeLabel)
        timeRangePanel.add(infoComboBox)
        contentPanel.add(timeRangePanel)

        // Add content panel to the top of the main panel
        mainPanel.add(contentPanel, BorderLayout.NORTH)
        return mainPanel
    }

    override fun isModified(): Boolean {
        val savedToken = CodetimePropertiesUtils.getToken()
        val savedTimeRange = CodetimePropertiesUtils.getTimeRange()
        return !savedToken.contentEquals(String(tokenField.password)) || infoComboBox.selectedItem != savedTimeRange
    }

    override fun apply() {
        CodetimePropertiesUtils.setToken(String(tokenField.password))
        CodetimePropertiesUtils.setTimeRange(infoComboBox.selectedItem as String)
    }


    override fun getDisplayName(): String = "Codetime Settings"
}


