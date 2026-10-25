package com.fameli.budget.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    private const val BASE_URL = "https://mastermitsu.ru/api.php"

    private fun log(message: String) = AppLogger.log("API", message)

    private fun openGet(urlStr: String): Pair<Int, String> {
        val conn = URL(urlStr).openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 15000
        conn.requestMethod = "GET"
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.bufferedReader()?.readText() ?: "[]"
        conn.disconnect()
        return code to text
    }

    private fun openPost(urlStr: String, body: String): Pair<Int, String> {
        val conn = URL(urlStr).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        conn.connectTimeout = 10000
        conn.readTimeout = 15000
        conn.doOutput = true
        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.bufferedReader()?.readText() ?: ""
        conn.disconnect()
        return code to text
    }

    /** Успех = HTTP 200 И body.success == true. Иначе outdated/ошибка. */
    private fun parseSaveResult(code: Int, text: String): Boolean {
        if (code != 200) return false
        return try {
            JSONObject(text).optBoolean("success", false)
        } catch (_: Exception) {
            false
        }
    }

    // ─── Transactions ───────────────────────────────────────────
    suspend fun getTransactions(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET transactions family=$familyId")
        val (code, text) = openGet("$BASE_URL?action=get_transactions&family_id=$familyId")
        log("GET transactions response: $code, length=${text.length}")
        if (code !in 200..299 || !text.trim().startsWith("[")) JSONArray() else JSONArray(text)
    }

    suspend fun saveTransaction(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE transaction: ${data.optString("cloud_id")}")
        try {
            val (code, text) = openPost("$BASE_URL?action=save_transaction", data.toString())
            val ok = parseSaveResult(code, text)
            log("SAVE transaction response: $code success=$ok")
            ok
        } catch (e: Exception) {
            log("SAVE transaction ERROR: ${e.message}")
            false
        }
    }

    // ─── Shopping ───────────────────────────────────────────────
    suspend fun getShopping(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET shopping family=$familyId")
        val (code, text) = openGet("$BASE_URL?action=get_shopping&family_id=$familyId")
        log("GET shopping response: $code, length=${text.length}")
        if (code !in 200..299 || !text.trim().startsWith("[")) JSONArray() else JSONArray(text)
    }

    suspend fun saveShopping(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE shopping: ${data.optString("cloud_id")}")
        try {
            val (code, text) = openPost("$BASE_URL?action=save_shopping", data.toString())
            val ok = parseSaveResult(code, text)
            log("SAVE shopping response: $code success=$ok")
            ok
        } catch (e: Exception) {
            log("SAVE shopping ERROR: ${e.message}")
            false
        }
    }

    // ─── Tasks ──────────────────────────────────────────────────
    suspend fun getTasks(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET tasks family=$familyId")
        val (code, text) = openGet("$BASE_URL?action=get_tasks&family_id=$familyId")
        log("GET tasks response: $code, length=${text.length}")
        if (code !in 200..299 || !text.trim().startsWith("[")) JSONArray() else JSONArray(text)
    }

    suspend fun saveTask(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE task: ${data.optString("cloud_id")}")
        try {
            val (code, text) = openPost("$BASE_URL?action=save_task", data.toString())
            val ok = parseSaveResult(code, text)
            log("SAVE task response: $code success=$ok")
            ok
        } catch (e: Exception) {
            log("SAVE task ERROR: ${e.message}")
            false
        }
    }

    // ─── Categories ─────────────────────────────────────────────
    suspend fun getCategories(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET categories family=$familyId")
        val (code, text) = openGet("$BASE_URL?action=get_categories&family_id=$familyId")
        if (code !in 200..299 || !text.trim().startsWith("[")) JSONArray() else JSONArray(text)
    }

    suspend fun saveCategory(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE category: ${data.optString("name")}")
        try {
            val (code, text) = openPost("$BASE_URL?action=save_category", data.toString())
            val ok = parseSaveResult(code, text)
            log("SAVE category response: $code success=$ok")
            ok
        } catch (e: Exception) {
            log("SAVE category ERROR: ${e.message}")
            false
        }
    }

    // ─── Budgets ────────────────────────────────────────────────
    suspend fun getBudgets(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET budgets family=$familyId")
        val (code, text) = openGet("$BASE_URL?action=get_budgets&family_id=$familyId")
        log("GET budgets response: $code, length=${text.length}")
        if (code !in 200..299 || !text.trim().startsWith("[")) JSONArray() else JSONArray(text)
    }

    suspend fun saveBudget(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE budget: ${data.optString("cloud_id")}")
        try {
            val (code, text) = openPost("$BASE_URL?action=save_budget", data.toString())
            val ok = parseSaveResult(code, text)
            log("SAVE budget response: $code success=$ok")
            ok
        } catch (e: Exception) {
            log("SAVE budget ERROR: ${e.message}")
            false
        }
    }

    // ─── Deletes ────────────────────────────────────────────────
    suspend fun deleteTransaction(cloudId: String, lastModified: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("cloud_id", cloudId); put("last_modified", lastModified)
            }
            val (code, text) = openPost("$BASE_URL?action=delete_transaction", body.toString())
            val ok = parseSaveResult(code, text)
            log("DELETE transaction $cloudId -> $ok")
            ok
        } catch (e: Exception) { log("DELETE transaction ERROR: ${e.message}"); false }
    }

    suspend fun deleteShopping(cloudId: String, lastModified: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("cloud_id", cloudId); put("last_modified", lastModified)
            }
            val (code, text) = openPost("$BASE_URL?action=delete_shopping", body.toString())
            val ok = parseSaveResult(code, text)
            log("DELETE shopping $cloudId -> $ok")
            ok
        } catch (e: Exception) { log("DELETE shopping ERROR: ${e.message}"); false }
    }

    suspend fun deleteTask(cloudId: String, lastModified: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("cloud_id", cloudId); put("last_modified", lastModified)
            }
            val (code, text) = openPost("$BASE_URL?action=delete_task", body.toString())
            val ok = parseSaveResult(code, text)
            log("DELETE task $cloudId -> $ok")
            ok
        } catch (e: Exception) { log("DELETE task ERROR: ${e.message}"); false }
    }

    suspend fun deleteCategory(cloudId: String, lastModified: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("cloud_id", cloudId); put("last_modified", lastModified)
            }
            val (code, text) = openPost("$BASE_URL?action=delete_category", body.toString())
            val ok = parseSaveResult(code, text)
            log("DELETE category $cloudId -> $ok")
            ok
        } catch (e: Exception) { log("DELETE category ERROR: ${e.message}"); false }
    }

    suspend fun deleteBudget(cloudId: String, lastModified: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("cloud_id", cloudId); put("last_modified", lastModified)
            }
            val (code, text) = openPost("$BASE_URL?action=delete_budget", body.toString())
            val ok = parseSaveResult(code, text)
            log("DELETE budget $cloudId -> $ok")
            ok
        } catch (e: Exception) { log("DELETE budget ERROR: ${e.message}"); false }
    }

    // ─── Families ───────────────────────────────────────────────
    suspend fun getFamilies(): JSONArray = withContext(Dispatchers.IO) {
        log("GET families")
        val (code, text) = openGet("$BASE_URL?action=get_families")
        if (code !in 200..299 || !text.trim().startsWith("[")) JSONArray() else JSONArray(text)
    }
}
