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

    suspend fun getTransactions(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL?action=get_transactions&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }

    suspend fun saveTransaction(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?action=save_transaction")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            conn.inputStream.close()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getCategories(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL?action=get_categories&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }

    suspend fun saveCategory(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?action=save_category")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            conn.inputStream.close()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getShopping(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL?action=get_shopping&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }

    suspend fun saveShopping(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?action=save_shopping")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            conn.inputStream.close()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getTasks(familyId: String): JSONArray = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL?action=get_tasks&family_id=$familyId")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }

    suspend fun saveTask(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?action=save_task")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            conn.inputStream.close()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getFamilies(): JSONArray = withContext(Dispatchers.IO) {
        val url = URL("$BASE_URL?action=get_families")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        val text = conn.inputStream.bufferedReader().readText()
        JSONArray(text)
    }

    suspend fun createFamily(data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL?action=create_family")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            OutputStreamWriter(conn.outputStream).use { it.write(data.toString()) }
            conn.inputStream.close()
            true
        } catch (e: Exception) { false }
    }
}
