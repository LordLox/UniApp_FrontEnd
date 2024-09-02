package com.example.uniapp.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.UserInfo
import com.example.uniapp.model.userTypeGson
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Base64

// Singleton object responsible for handling user login and password-related operations with the API
object LoginApiService {

    // Function to authenticate a user by their username and password
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loginUser(username: String, password: String): UserInfo {
        // Create the Basic Authentication string by encoding the username and password
        val credential = "$username:$password"
        val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credential.toByteArray())

        // Build the HTTP GET request to retrieve user information
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/users/userinfo") // API endpoint for user info
            .addHeader("Authorization", basicAuth) // Add Basic Authentication header
            .get() // GET request method
            .build()

        // Execute the request on the IO dispatcher (for network operations)
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Retrieve the encrypted user info from the response body, or throw an exception if null
        val encryptedUserInfo = response.body?.string() ?: throw Exception("No User info data available")

        // Close the response body to free resources
        response.body?.close()

        // Save the encrypted user information to a file using FileStorageUtils
        FileStorageUtils.saveToFile(GlobalUtils.applicationPath, GlobalUtils.userInfoFileName, encryptedUserInfo)

        // Decrypt the user info and return the resulting UserInfo object
        return decryptUserInfo(encryptedUserInfo)
    }

    // Function to change the user's password
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun changePassword(newPassword: String): String {
        // Set the content type for the request body as plain text
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = newPassword.toRequestBody(mediaType) // Convert the new password to request body
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/users/changepass") // API endpoint for password change
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth) // Add Basic Authentication header
            .addHeader("Content-Type", "text/plain") // Set Content-Type header
            .post(requestBody) // POST request method
            .build()

        // Execute the request on the IO dispatcher
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Retrieve the response body as a string
        val responseBody = response.body?.string()

        // Close the response body to free resources
        response.body?.close()

        // Return the response body content, or an empty string if the body is null
        return responseBody ?: ""
    }

    // Function to decrypt the user info received from the server
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun decryptUserInfo(encryptedUserInfo: String): UserInfo {
        // Set the content type for the request body as plain text
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = encryptedUserInfo.toRequestBody(mediaType) // Convert encrypted info to request body
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/barcode/decrypt") // API endpoint for decryption
            .addHeader("Content-Type", "text/plain") // Set Content-Type header
            .post(requestBody) // POST request method
            .build()

        // Execute the request on the IO dispatcher
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Retrieve the decrypted user info from the response body, or throw an exception if null
        val decryptedUserInfo = response.body?.string() ?: throw Exception("Unable to read userinfo")

        // Close the response body to free resources
        response.body?.close()

        // Deserialize the decrypted user info JSON into a UserInfo object and return it
        val jsonVal = userTypeGson().fromJson(decryptedUserInfo, UserInfo::class.java)
        return jsonVal
    }
}
