package com.fameli.budget

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.fameli.budget.data.remote.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    private const val CURRENT_VERSION = "v1.10.13"
    private const val REPO = "Mitsubishimas/fameli"

    private fun log(msg: String) = AppLogger.log("UPDATE", msg)

    fun check(context: Context, showDialog: Boolean = true) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                log("Проверка обновлений. Версия: $CURRENT_VERSION")
                val url = URL("https://raw.githubusercontent.com/$REPO/main/version.txt")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                val latest = conn.inputStream.bufferedReader().readText().trim()
                conn.disconnect()
                log("На сервере: $latest")

                withContext(Dispatchers.Main) {
                    if (latest.isNotEmpty() && latest != CURRENT_VERSION.removePrefix("v")) {
                        log("Доступна версия $latest")
                        downloadApk(appContext, latest)
                    } else if (showDialog) {
                        Toast.makeText(appContext, "У вас последняя версия ($CURRENT_VERSION)", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                log("Ошибка: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadApk(context: Context, version: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
            Toast.makeText(context, "Разрешите установку", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                log("Получение URL...")
                val apiUrl = URL("https://api.github.com/repos/$REPO/releases/latest")
                val conn = apiUrl.openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.setRequestProperty("User-Agent", "Fameli-App")
                val json = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val marker = "\"browser_download_url\":\""
                val start = json.indexOf(marker)
                if (start == -1) { log("URL не найден"); return@launch }
                val urlStart = start + marker.length
                val urlEnd = json.indexOf("\"", urlStart)
                val downloadUrl = json.substring(urlStart, urlEnd)
                log("Скачивание: $downloadUrl")

                val file = File(context.externalCacheDir, "Fameli-Update.apk")
                if (file.exists()) file.delete()

                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val request = DownloadManager.Request(Uri.parse(downloadUrl))
                request.setTitle("Fameli")
                request.setDescription("v$version")
                request.setDestinationUri(Uri.fromFile(file))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                val downloadId = dm.enqueue(request)
                log("Загрузка ID: $downloadId")

                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(ctx: Context, intent: Intent) {
                        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (id == downloadId) {
                            ctx.unregisterReceiver(this)
                            log("Загружено: ${file.length()} байт")
                            if (file.exists() && file.length() > 1000000) installApk(ctx, file)
                            else log("Файл повреждён")
                        }
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
                } else {
                    context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                }
            } catch (e: Exception) {
                log("Ошибка скачивания: ${e.message}")
            }
        }
    }

    private fun installApk(context: Context, file: File) {
        try {
            log("Установка...")
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) { log("Ошибка установки: ${e.message}") }
    }
}
