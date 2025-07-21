package dev.codetime

import com.intellij.ide.util.PropertiesComponent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport


object CodetimePropertiesUtils {
    private const val TOKEN_KEY = "token"
    private const val TIME_RANGE_KEY = "timeRange"
    private val propertyChangeSupport: PropertyChangeSupport = PropertyChangeSupport(this)


    fun getToken(): String {
        return PropertiesComponent.getInstance().getValue(TOKEN_KEY) ?: ""
    }

    fun setToken(token: String) {
        val oldValue = this.getToken()
        PropertiesComponent.getInstance().setValue(TOKEN_KEY, token)
        propertyChangeSupport.firePropertyChange(TOKEN_KEY, oldValue, token)
    }

    fun getTimeRange(): String {
        return PropertiesComponent.getInstance().getValue(TIME_RANGE_KEY) ?: "Total code time"
    }

    fun setTimeRange(timeRange: String) {
        val oldValue = this.getTimeRange()
        PropertiesComponent.getInstance().setValue(TIME_RANGE_KEY, timeRange)
        propertyChangeSupport.firePropertyChange(TIME_RANGE_KEY, oldValue, timeRange)
    }

    // 添加监听器
    fun addListener(listener: PropertyChangeListener?) {
        propertyChangeSupport.addPropertyChangeListener(listener)
    }

    // 移除监听器
    fun removeListener(listener: PropertyChangeListener?) {
        propertyChangeSupport.removePropertyChangeListener(listener)
    }

}