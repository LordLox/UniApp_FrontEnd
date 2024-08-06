package com.example.uniapp.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.example.uniapp.util.GlobalVariables

class EventApiService(context: Context) {

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val createUrl = "${GlobalVariables.apiCommonUrl}events"
    private val updateUrl = "${GlobalVariables.apiCommonUrl}events/1"
    private val getEventUrl = "${GlobalVariables.apiCommonUrl}events/3"

    fun fetchEventDetails(eventId: Int, onSuccess: (response: JSONObject) -> Unit, onError: (error: Throwable) -> Unit) {
        val url = "$getEventUrl/$eventId"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response -> onSuccess(response) },
            { error -> onError(error) })
        requestQueue.add(request)
    }

    fun createEvent(eventDetails: JSONObject, onSuccess: (response: JSONObject) -> Unit, onError: (error: Throwable) -> Unit) {
        val request = JsonObjectRequest(Request.Method.POST, createUrl, eventDetails,
            { response -> onSuccess(response) },
            { error -> onError(error) })
        requestQueue.add(request)
    }

    fun updateEvent(eventDetails: JSONObject, onSuccess: (response: JSONObject) -> Unit, onError: (error: Throwable) -> Unit) {
        val request = JsonObjectRequest(Request.Method.PUT, updateUrl, eventDetails,
            { response -> onSuccess(response) },
            { error -> onError(error) })
        requestQueue.add(request)
    }
}
