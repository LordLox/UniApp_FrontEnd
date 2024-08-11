package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.Base64

object EventListApiService {
    private val client = OkHttpClient()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventsFromAPI(): JSONArray = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("")
            .addHeader("Authorization", "")
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
