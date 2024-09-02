package com.example.uniapp.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.BarcodeDataDto
import com.example.uniapp.model.eventTypeGson
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception

// Singleton object responsible for handling QR code-related operations with the API
object QrCodeApiService {

    // Function to generate a QR code and return it as a Bitmap image
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateQrCode(): Bitmap {
        // Build the HTTP GET request to generate the QR code
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/barcode/qr") // API endpoint for QR code generation
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth) // Add Basic Authentication header
            .get() // GET request method
            .build()

        // Execute the request on the IO dispatcher (for network operations)
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Get the QR code image from the response body as a byte stream and decode it into a Bitmap
        val responseBody = response.body?.byteStream()
        val bitmap = responseBody?.let { BitmapFactory.decodeStream(it) }

        // Close the response body to free resources
        response.body?.close()

        // Return the Bitmap, or throw an exception if the Bitmap is null (indicating a failure)
        return bitmap ?: throw Exception("Failed to generate QR code: ${response.code}")
    }

    // Function to decrypt a QR code and return the resulting BarcodeDataDto object
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun decryptQrCode(encryptedBarcodeData: String): BarcodeDataDto {
        // Set the content type for the request body as plain text
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = encryptedBarcodeData.toRequestBody(mediaType) // Convert encrypted barcode data to request body
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/barcode/decrypt") // API endpoint for decrypting barcode data
            .addHeader("Content-Type", "text/plain") // Set Content-Type header
            .post(requestBody) // POST request method
            .build()

        // Execute the request on the IO dispatcher
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Get the decrypted barcode data as a string from the response body
        val decryptedBarcodeData = response.body?.string() ?: throw Exception("Unable to read barcode data")

        // Close the response body to free resources
        response.body?.close()

        // Deserialize the decrypted data from JSON into a BarcodeDataDto object using Gson
        val jsonVal = eventTypeGson().fromJson(decryptedBarcodeData, BarcodeDataDto::class.java)
        return jsonVal
    }

    // Function to register a presence by sending a QR code and event ID to the API
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertPresenceQrCode(encryptedBarcodeData: String, eventId: Int) {
        // Set the content type for the request body as plain text
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = encryptedBarcodeData.toRequestBody(mediaType) // Convert encrypted barcode data to request body
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/events/entry/$eventId") // API endpoint for event entry
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth) // Add Basic Authentication header
            .addHeader("Content-Type", "application/octet-stream") // Set Content-Type header
            .post(requestBody) // POST request method
            .build()

        // Execute the request on the IO dispatcher
        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        // Check if the response code is not 201 (Created), and throw an exception if it's not
        if(response.code != 201){
            throw Exception("Unable to register presence, error ${response.code}")
        }
    }
}
