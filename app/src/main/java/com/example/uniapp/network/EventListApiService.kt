package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.Base64

class EventListApiService(private val apiUrl: String) {
    private val client = OkHttpClient()
    private val credential = "admin:w%Yr*dV^3%Euync9yLka62C$"

    @RequiresApi(Build.VERSION_CODES.O)
    private val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credential.toByteArray())

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventsFromAPI(): JSONArray = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", basicAuth)
            .build()
        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    return@withContext JSONArray(responseBody)
                }
            } else {
                println("Request not successful: ${response.code}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext JSONArray()
    }
}
