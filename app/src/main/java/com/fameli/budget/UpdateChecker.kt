package com.fameli.budget

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

object UpdateChecker {
    private const val CURRENT_VERSION = "v1.1.5"
    private const val REPO = "Mitsubishimas/fameli"
    private const val PREFS_NAME = "fameli_update"

    fun check(context: Context, showToast: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs: android.content.SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastCheck: Long = prefs.getLong("last_check", 0L)
                val weekInMs: Long = 7L * 24L * 60L * 60L * 1000L

                if (!showToast && System.currentTimeMillis() - lastCheck < weekInMs) return@launch

                val url: URL = URL("https://api.github.com/repos/$REPO/releases/latest")
                val json: String = url.readText()

                val tagStart: Int = json.indexOf("\"tag_name\":\"") + 12
                val tagEnd: Int = json.indexOf("\"", tagStart)
                val latestVersion: String = json.substring(tagStart, tagEnd)

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
        val s: List<Int> = server.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        val c: List<Int> = current.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(s.size, c.size)) {
            val sv: Int = s.getOrElse(i) { 0 }
            val cv: Int = c.getOrElse(i) { 0 }
            if (sv > cv) return true
            if (sv < cv) return false
        }
        return false
    }

    private fun showUpdateDialog(context: Context, version: String) {
        AlertDialog.Builder(context)
            .setTitle("Доступно обновление")
            .setMessage("Новая версия: $version\nСкачать и установить?")
            .setPositiveButton("Скачать") { _, _ -> downloadApk(context) }
            .setNegativeButton("Позже", null)
            .show()
    }

    private fun downloadApk(context: Context) {
        val downloadUrl: String = "https://github.com/$REPO/releases/latest/download/app-debug.apk"
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("Fameli")
            .setDescription("Скачивание обновления...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Fameli-Update.apk")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val dm: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}
