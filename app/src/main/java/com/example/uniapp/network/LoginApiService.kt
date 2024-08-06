package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import java.io.IOException
import java.util.Base64

class LoginApiService(private val apiUrl: String) {

    private val client = OkHttpClient()

    @RequiresApi(Build.VERSION_CODES.O)
    fun loginUser(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        val credential = "$username:$password"
        val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credential.toByteArray())

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", basicAuth)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, response.body?.string())
                } else {
                    callback(false, null)
                }
            }
        })
    }
}
