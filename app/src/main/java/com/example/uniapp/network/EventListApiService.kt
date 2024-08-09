package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.Base64

class EventListApiService (private val apiUrl: String) {
    private val client = OkHttpClient()
    val credential = "admin:w%Yr*dV^3%Euync9yLka62C$"

    @RequiresApi(Build.VERSION_CODES.O)
    val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credential.toByteArray())

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEventsFromAPI(): JSONArray {
        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", basicAuth)
            .build()
        client.newCall(request)
        return JSONArray()
    }
}