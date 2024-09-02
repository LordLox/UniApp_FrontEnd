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

// Singleton object that provides methods to interact with the Event API
object EventApiService {

    // Base URL for the events endpoint, derived from a global utility
    private val eventUrl = "${GlobalUtils.apiCommonUrl}/events"

    // HTTP client for making requests, obtained from a global utility
    private val httpClient: OkHttpClient = GlobalUtils.httpClient

    // Gson instance configured with a custom deserializer for EventType
    private val gson: Gson = eventTypeGson()

    // Function to fetch event details by event ID
    // Requires Android Oreo (API level 26) or higher for Base64 encoding in Basic Auth
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchEventDetails(eventId: Int): EventDto? {
        // Build the HTTP GET request with the event ID and necessary headers
        val request = Request.Builder()
            .url("$eventUrl/$eventId")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        // Execute the request and deserialize the JSON response into an EventDto object
        val eventInfo = executeRequest(request)
        return gson.fromJson(eventInfo, EventDto::class.java)
    }

    // Function to create a new event
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createEvent(eventDto: EventDto): Int? {
        // Convert the event object to JSON
        val eventJson = gson.toJson(eventDto)

        // Create a request body with the JSON payload and set the appropriate content type
        val requestBody = eventJson.toRequestBody("application/json".toMediaTypeOrNull())

        // Build the HTTP POST request to create the event
        val request = Request.Builder()
            .url(eventUrl)
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        // Execute the request and parse the response as an integer (e.g., event ID)
        val response = executeRequest(request)
        return response?.toIntOrNull()
    }

    // Function to update an existing event
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateEvent(eventDto: EventDto) {
        // Convert the event object to JSON
        val eventJson = gson.toJson(eventDto)

        // Create a request body with the JSON payload and set the appropriate content type
        val requestBody = eventJson.toRequestBody("application/json".toMediaTypeOrNull())

        // Build the HTTP PATCH request to update the event
        val request = Request.Builder()
            .url("$eventUrl/${eventDto.id}")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/json")
            .patch(requestBody)
            .build()

        // Execute the request (response is not returned or used here)
        executeRequest(request)
    }

    // Function to delete an event by its ID
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteEvent(eventId: Int) {
        // Build the HTTP DELETE request with the event ID and necessary headers
        val request = Request.Builder()
            .url("$eventUrl/$eventId")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .delete()
            .build()

        // Execute the request (response is not returned or used here)
        executeRequest(request)
    }

    // Function to fetch event history within a specific time range
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchEventHistory(
        startTimestamp: Long,  // Start time in milliseconds since epoch
        endTimestamp: Long,    // End time in milliseconds since epoch
        timezone: String = "Europe/Rome",  // Timezone for the request
        culture: String = "it-IT"  // Culture/locale for the request
    ): String? {
        // Build the HTTP GET request with the specified parameters and headers
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

        // Execute the request and return the response body as a string
        val response = executeRequest(request)
        return response
    }

    // Helper function to execute an HTTP request and return the response as a string
    private suspend fun executeRequest(request: Request): String? {
        // Use a coroutine to perform the network operation on the IO dispatcher
        val response: Response = withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute()
        }

        // Extract the response body as a string
        val responseBody = response.body?.string()

        // Close the response body to free resources
        response.body?.close()

        // Return the response body if the request was successful, otherwise throw an exception
        return if (response.isSuccessful) {
            responseBody
        } else {
            throw Exception("Request failed with status code: ${response.code}")
        }
    }
}
