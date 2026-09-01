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
    private const val CURRENT_VERSION = "v1.9.2"
    private const val REPO = "Mitsubishimas/fameli"

    private fun log(msg: String) = AppLogger.log("UPDATE", msg)

    fun check(context: Context, showDialog: Boolean = true) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                log("Проверка обновлений. Текущая версия: $CURRENT_VERSION")
                val url = URL("https://raw.githubusercontent.com/$REPO/main/version.txt")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                val latestVersion = connection.inputStream.bufferedReader().readText().trim()
                connection.disconnect()
                
                log("Версия на сервере: $latestVersion")

                withContext(Dispatchers.Main) {
                    if (latestVersion.isNotEmpty() && latestVersion != CURRENT_VERSION.removePrefix("v")) {
                        log("Доступна новая версия: $latestVersion")
                        downloadApk(appContext, latestVersion)
                    } else if (showDialog) {
                        log("У вас последняя версия")
                        Toast.makeText(appContext, "У вас последняя версия ($CURRENT_VERSION)", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                log("Ошибка проверки: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadApk(context: Context, version: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
            log("Нет разрешения на установку")
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
                log("Получение URL для скачивания...")
                val apiUrl = URL("https://api.github.com/repos/$REPO/releases/latest")
                val apiConnection = apiUrl.openConnection() as HttpURLConnection
                apiConnection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                apiConnection.setRequestProperty("User-Agent", "Fameli-App")
                val json = apiConnection.inputStream.bufferedReader().readText()
                apiConnection.disconnect()

                val marker = "\"browser_download_url\":\""
                val startIdx = json.indexOf(marker)
                if (startIdx == -1) {
                    log("URL для скачивания не найден")
                    return@launch
                }
                val urlStart = startIdx + marker.length
                val urlEnd = json.indexOf("\"", urlStart)
                val downloadUrl = json.substring(urlStart, urlEnd)
                
                log("Скачивание: $downloadUrl")

                val file = File(context.externalCacheDir, "Fameli-Update.apk")
                if (file.exists()) file.delete()

                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val request = DownloadManager.Request(Uri.parse(downloadUrl))
                request.setTitle("Fameli")
                request.setDescription("Скачивание v$version...")
                request.setDestinationUri(Uri.fromFile(file))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                val downloadId = dm.enqueue(request)
                log("Загрузка началась, ID: $downloadId")

                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(ctx: Context, intent: Intent) {
                        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (id == downloadId) {
                            ctx.unregisterReceiver(this)
                            log("Загрузка завершена, размер: ${file.length()}")
                            if (file.exists() && file.length() > 1000000) {
                                installApk(ctx, file)
                            } else {
                                log("Файл повреждён")
                            }
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
            log("Установка APK...")
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else {
                Uri.fromFile(file)
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            log("Ошибка установки: ${e.message}")
        }
    }
}
