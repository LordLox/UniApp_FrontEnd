package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.EventDto
import com.example.uniapp.model.eventTypeGson
import com.example.uniapp.model.userTypeGson
import com.example.uniapp.util.GlobalUtils
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.Base64

object EventListApiService {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchEvents(): List<EventDto> {
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/events/personal")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .get()
            .build()

        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        val responseBody = response.body?.string() ?: throw Exception("Unable to fetch events")

        response.body?.close()

        val eventsListType = object : TypeToken<List<EventDto>>() {}.type
        val events: List<EventDto> = eventTypeGson().fromJson(responseBody, eventsListType)

        return events
    }
}
