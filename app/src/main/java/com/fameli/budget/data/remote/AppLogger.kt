package com.fameli.budget.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppLogger {
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs

    fun log(tag: String, message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        _logs.value = (_logs.value + "[$timestamp] [$tag] $message").takeLast(100)
    }

    fun clear() {
        _logs.value = emptyList()
    }

    fun getLogs(): String = _logs.value.joinToString("\n")
}
