package com.fameli.budget

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

object UpdateChecker {
    private const val CURRENT_VERSION = "v1.1.7"  // Менять при КАЖДОМ релизе!
    private const val REPO = "Mitsubishimas/fameli"

    fun check(context: Context, showDialog: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs: SharedPreferences = context.getSharedPreferences("fameli_update", Context.MODE_PRIVATE)
                val lastCheck: Long = prefs.getLong("last_update_check", 0)
                val weekInMillis: Long = 7 * 24 * 60 * 60 * 1000L

                if (!showDialog && (System.currentTimeMillis() - lastCheck) < weekInMillis) return@launch

                val json: String = URL("https://api.github.com/repos/$REPO/releases/latest").readText()
                val tagStart: Int = json.indexOf("\"tag_name\":\"") + 12
                val tagEnd: Int = json.indexOf("\"", tagStart)
                val serverVersion: String = json.substring(tagStart, tagEnd)

                prefs.edit().putLong("last_update_check", System.currentTimeMillis()).apply()

                if (serverVersion != CURRENT_VERSION) {
                    withContext(Dispatchers.Main) {
                        showUpdateDialog(context, serverVersion)
                    }
                } else if (showDialog) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "У вас последняя версия", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                if (showDialog) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Не удалось проверить обновления", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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
        val dm: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request: DownloadManager.Request = DownloadManager.Request(
            Uri.parse("https://github.com/$REPO/releases/latest/download/app-debug.apk")
        )
        request.setTitle("Fameli")
        request.setDescription("Скачивание обновления...")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Fameli-Update.apk")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        dm.enqueue(request)
    }
}
