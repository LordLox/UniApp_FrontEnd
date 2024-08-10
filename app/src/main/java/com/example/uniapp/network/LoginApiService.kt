package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.UserInfo
import com.example.uniapp.model.getGson
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalVariables
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Base64

class LoginApiService() {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loginUser(username: String, password: String): UserInfo {
        val credential = "$username:$password"
        val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credential.toByteArray())

        val request = Request.Builder()
            .url("${GlobalVariables.apiCommonUrl}/users/userinfo")
            .addHeader("Authorization", basicAuth)
            .get()
            .build()

        val response = withContext(Dispatchers.IO) {
            GlobalVariables.httpClient.newCall(request).execute()
        }

        val encryptedUserInfo = response.body?.string() ?: throw Exception("No User info data available")

        response.body?.close()

        FileStorageUtils.saveToFile(GlobalVariables.userInfoFileName, encryptedUserInfo)

        return decryptUserInfo(encryptedUserInfo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun decryptUserInfo(encryptedUserInfo: String): UserInfo {
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = encryptedUserInfo.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("${GlobalVariables.apiCommonUrl}/barcode/decrypt")
            .addHeader("Content-Type", "text/plain")
            .post(requestBody)
            .build()

        val response = withContext(Dispatchers.IO) {
            GlobalVariables.httpClient.newCall(request).execute()
        }
        val decryptedUserInfo = response.body?.string() ?: throw Exception("Unable to read userinfo")

        response.body?.close()
        val gson = getGson()
        val jsonVal = gson.fromJson(decryptedUserInfo, UserInfo::class.java)
        return jsonVal
    }
}
