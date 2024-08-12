package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.EventDto
import com.example.uniapp.model.HistoryDto
import com.example.uniapp.model.UserInfo
import com.example.uniapp.util.GlobalUtils
import com.example.uniapp.model.eventTypeGson
import com.example.uniapp.model.userTypeGson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EventApiService {

    private val eventUrl = "${GlobalUtils.apiCommonUrl}/events"
    private val httpClient: OkHttpClient = GlobalUtils.httpClient
    private val gson: Gson = eventTypeGson()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchEventDetails(eventId: Int): EventDto? {
         val request = Request.Builder()
            .url("$eventUrl/$eventId")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/json")
            .get()
            .build()
        val eventInfo = executeRequest(request)
        return gson.fromJson(eventInfo, EventDto::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createEvent(eventDto: EventDto): Int? {
        val eventJson = gson.toJson(eventDto)
        val requestBody = eventJson.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(eventUrl)
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = executeRequest(request)
        return response?.toIntOrNull()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateEvent(eventDto: EventDto) {
        val eventJson = gson.toJson(eventDto)
        val requestBody = eventJson.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("$eventUrl/${eventDto.id}")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/json")
            .patch(requestBody)
            .build()

        executeRequest(request)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteEvent(eventId: Int) {
        val request = Request.Builder()
            .url("$eventUrl/$eventId")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .delete()
            .build()

        executeRequest(request)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchEventHistory(
        startTimestamp: Long,
        endTimestamp: Long,
        timezone: String = "Europe/Rome",
        culture: String = "it-IT"
    ): List<HistoryDto>? {
        val request = Request.Builder()
            .url("$eventUrl/entry/history")
            .addHeader("Content-Type", "text/csv")
            .addHeader("Timezone", timezone)
            .addHeader("Culture", culture)
            .addHeader("StartTimestamp", startTimestamp.toString())
            .addHeader("EndTimestamp", endTimestamp.toString())
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .get()
            .build()

        val response = executeRequest(request)
        return response?.let {
            val listType = object : TypeToken<List<HistoryDto>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    private suspend fun executeRequest(request: Request): String? {
        val response: Response = withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute()
        }

        val responseBody = response.body?.string()

        response.body?.close()

        return if (response.isSuccessful) {
            responseBody
        } else {
            throw Exception()
        }
    }
}
