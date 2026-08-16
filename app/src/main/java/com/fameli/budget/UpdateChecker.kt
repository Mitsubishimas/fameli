package com.fameli.budget

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    private const val CURRENT_VERSION = "v1.5.1"
    private const val REPO = "Mitsubishimas/fameli"

    fun check(context: Context, showDialog: Boolean = true) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://raw.githubusercontent.com/$REPO/main/version.txt")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val latestVersion = connection.inputStream.bufferedReader().readText().trim()
                connection.disconnect()

                withContext(Dispatchers.Main) {
                    if (latestVersion.isNotEmpty() && latestVersion != CURRENT_VERSION.removePrefix("v")) {
                        showUpdateDialog(appContext, latestVersion)
                    } else if (showDialog) {
                        Toast.makeText(appContext, "У вас последняя версия", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Не удалось проверить: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showUpdateDialog(context: Context, version: String) {
        try {
            AlertDialog.Builder(context)
                .setTitle("Доступно обновление")
                .setMessage("Новая версия: $version\nТекущая: $CURRENT_VERSION")
                .setPositiveButton("Скачать") { _, _ -> openDownloadPage(context) }
                .setNegativeButton("Позже", null)
                .show()
        } catch (e: Exception) {
            // Если диалог не получился — открываем браузер
            openDownloadPage(context)
        }
    }

    private fun openDownloadPage(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/$REPO/releases/latest"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Откройте GitHub вручную", Toast.LENGTH_LONG).show()
        }
    }
}
