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

    private fun log(message: String) {
        AppLogger.log("API", message)
    }

    suspend fun getTransactions(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET transactions family=$familyId")
        val url = URL("$BASE_URL?action=get_transactions&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val code = conn.responseCode
        val text = conn.inputStream.bufferedReader().readText()
        log("GET transactions response: $code, length=${text.length}")
        JSONArray(text)
    }

    suspend fun saveTransaction(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE transaction: ${data.optString("cloud_id")}")
        try {
            val url = URL("$BASE_URL?action=save_transaction")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            val code = conn.responseCode
            log("SAVE transaction response: $code")
            conn.inputStream.close()
            code == 200
        } catch (e: Exception) {
            log("SAVE transaction ERROR: ${e.message}")
            false
        }
    }

    suspend fun getShopping(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET shopping family=$familyId")
        val url = URL("$BASE_URL?action=get_shopping&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        log("GET shopping response length=${text.length}")
        JSONArray(text)
    }

    suspend fun saveShopping(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE shopping: ${data.optString("cloud_id")} purchased=${data.optInt("is_purchased", 0)}")
        try {
            val url = URL("$BASE_URL?action=save_shopping")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            val code = conn.responseCode
            log("SAVE shopping response: $code")
            conn.inputStream.close()
            code == 200
        } catch (e: Exception) {
            log("SAVE shopping ERROR: ${e.message}")
            false
        }
    }

    suspend fun getTasks(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET tasks family=$familyId")
        val url = URL("$BASE_URL?action=get_tasks&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        log("GET tasks response length=${text.length}")
        JSONArray(text)
    }

    suspend fun saveTask(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE task: ${data.optString("cloud_id")} completed=${data.optInt("is_completed", 0)}")
        try {
            val url = URL("$BASE_URL?action=save_task")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            val code = conn.responseCode
            log("SAVE task response: $code")
            conn.inputStream.close()
            code == 200
        } catch (e: Exception) {
            log("SAVE task ERROR: ${e.message}")
            false
        }
    }

    suspend fun getCategories(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        log("GET categories family=$familyId")
        val url = URL("$BASE_URL?action=get_categories&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }

    suspend fun saveCategory(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        log("SAVE category: ${data.optString("name")}")
        try {
            val url = URL("$BASE_URL?action=save_category")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            val code = conn.responseCode
            log("SAVE category response: $code")
            conn.inputStream.close()
            code == 200
        } catch (e: Exception) {
            log("SAVE category ERROR: ${e.message}")
            false
        }
    }

    suspend fun getFamilies(): JSONArray = withContext(Dispatchers.IO) {
        log("GET families")
        val url = URL("$BASE_URL?action=get_families")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }
}
