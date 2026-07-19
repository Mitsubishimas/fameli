package com.fameli.budget

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object UpdateChecker {
    private const val CURRENT_VERSION = "v1.1.5"  // Менять при КАЖДОМ релизе!
    private const val REPO = "Mitsubishimas/fameli"
    private const val PREFS_NAME = "fameli_update"

    fun check(context: Context, showToast: Boolean = true) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastCheck = prefs.getLong("last_check", 0)
                val weekInMs = 7 * 24 * 60 * 60 * 1000L

                // Проверяем не чаще раза в неделю
                if (!showToast && System.currentTimeMillis() - lastCheck < weekInMs) return@launch

                val url = URL("https://api.github.com/repos/$REPO/releases/latest")
                val json = url.readText()
                
                // Достаём tag_name из JSON
                val tagStart = json.indexOf("\"tag_name\":\"") + 12
                val tagEnd = json.indexOf("\"", tagStart)
                val latestVersion = json.substring(tagStart, tagEnd)

                prefs.edit().putLong("last_check", System.currentTimeMillis()).apply()

                if (latestVersion != CURRENT_VERSION && isNewer(latestVersion, CURRENT_VERSION)) {
                    withContext(Dispatchers.Main) {
                        showUpdateDialog(context, latestVersion)
                    }
                } else if (showToast) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "У вас последняя версия", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                if (showToast) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Не удалось проверить обновления", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun isNewer(server: String, current: String): Boolean {
        val s = server.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        val c = current.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(s.size, c.size)) {
            val sv = s.getOrElse(i) { 0 }
            val cv = c.getOrElse(i) { 0 }
            if (sv > cv) return true
            if (sv < cv) return false
        }
        return false
    }

    private fun showUpdateDialog(context: Context, version: String) {
        android.app.AlertDialog.Builder(context)
            .setTitle("Доступно обновление")
            .setMessage("Новая версия: $version\nСкачать и установить?")
            .setPositiveButton("Скачать") { _, _ ->
                downloadApk(context)
            }
            .setNegativeButton("Позже", null)
            .show()
    }

    private fun downloadApk(context: Context) {
        val url = "https://github.com/$REPO/releases/latest/download/app-debug.apk"
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Fameli")
            .setDescription("Скачивание обновления...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Fameli-Update.apk")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}

// Kotlin coroutines
private fun kotlinx.coroutines.CoroutineScope.launch(
    context: kotlinx.coroutines.CoroutineDispatcher,
    block: suspend () -> Unit
) = kotlinx.coroutines.CoroutineScope(context).launch { block() }
