package com.fameli.budget

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

object UpdateChecker {
    private const val CURRENT_VERSION = "v1.4.5"
    private const val REPO = "Mitsubishimas/fameli"

    fun check(context: Context, showDialog: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Читаем версию из version.txt (всегда работает)
                val latestVersion: String = URL("https://raw.githubusercontent.com/$REPO/main/version.txt")
                    .readText()
                    .trim()
                    .removePrefix("v")

                if (latestVersion.isNotEmpty() && latestVersion != CURRENT_VERSION.removePrefix("v")) {
                    if (isNewVersion(CURRENT_VERSION, "v$latestVersion")) {
                        withContext(Dispatchers.Main) {
                            showUpdateDialog(context, latestVersion)
                        }
                    }
                } else if (showDialog) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "У вас последняя версия ($CURRENT_VERSION)", Toast.LENGTH_SHORT).show()
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

    private fun isNewVersion(current: String, server: String): Boolean {
        return try {
            val cur = current.removePrefix("v").split(".").map { it.toInt() }
            val srv = server.removePrefix("v").split(".").map { it.toInt() }
            for (i in 0 until minOf(cur.size, srv.size)) {
                if (srv[i] > cur[i]) return true
                if (srv[i] < cur[i]) return false
            }
            srv.size > cur.size
        } catch (e: Exception) { false }
    }

    private fun showUpdateDialog(context: Context, version: String) {
        AlertDialog.Builder(context)
            .setTitle("Доступно обновление")
            .setMessage("Новая версия: $version\nТекущая: $CURRENT_VERSION")
            .setPositiveButton("Скачать") { _, _ -> downloadAndInstall(context) }
            .setNegativeButton("Позже", null)
            .setCancelable(false)
            .show()
    }

    private fun downloadAndInstall(context: Context) {
        try {
            val file = File(context.externalCacheDir, "Fameli-Update.apk")
            if (file.exists()) file.delete()

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse("https://github.com/$REPO/releases/latest/download/app-debug.apk"))
            request.setTitle("Fameli")
            request.setDescription("Скачивание обновления...")
            request.setDestinationUri(Uri.fromFile(file))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadId = dm.enqueue(request)

            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        ctx.unregisterReceiver(this)
                        installApk(ctx, file)
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            }
            Toast.makeText(context, "Загрузка началась...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        }
    }

    private fun installApk(context: Context, file: File) {
        try {
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
            Toast.makeText(context, "Ошибка установки", Toast.LENGTH_SHORT).show()
        }
    }
}
