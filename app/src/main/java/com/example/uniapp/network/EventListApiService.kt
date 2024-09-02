package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.EventDto
import com.example.uniapp.model.eventTypeGson
import com.example.uniapp.util.GlobalUtils
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

// Singleton object that provides methods to interact with the API for fetching a list of events
object EventListApiService {

    // Function to fetch a list of events
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchEvents(): List<EventDto> {
        // Build the HTTP GET request to fetch the user's personal events
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/events/personal") // API endpoint for personal events
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth) // Add Basic Authentication header
            .get() // GET request method
            .build()

        // Execute the request on the IO dispatcher (for network operations)
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Retrieve the response body as a string, or throw an exception if null
        val responseBody = response.body?.string() ?: throw Exception("Unable to fetch events")

        // Close the response body to free resources
        response.body?.close()

        // Define the type of the list of EventDto objects for Gson deserialization
        val eventsListType = object : TypeToken<List<EventDto>>() {}.type

        // Deserialize the JSON response into a list of EventDto objects using Gson
        val events: List<EventDto> = eventTypeGson().fromJson(responseBody, eventsListType)

        // Return the list of events
        return events
    }
}
