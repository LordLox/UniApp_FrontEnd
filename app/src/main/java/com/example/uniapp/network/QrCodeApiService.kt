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

object QrCodeApiService {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateQrCode(): Bitmap {
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/barcode/qr")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .get()
            .build()

        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        val responseBody = response.body?.byteStream()
        val bitmap = responseBody?.let { BitmapFactory.decodeStream(it) }

        response.body?.close()

        return bitmap ?: throw Exception("Failed to generate QR code: ${response.code}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun decryptQrCode(encryptedBarcodeData: String): BarcodeDataDto {
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = encryptedBarcodeData.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/barcode/decrypt")
            .addHeader("Content-Type", "text/plain")
            .post(requestBody)
            .build()

        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }
        val decryptedBarcodeData = response.body?.string() ?: throw Exception("Unable to read barcode data")

        response.body?.close()
        val jsonVal = eventTypeGson().fromJson(decryptedBarcodeData, BarcodeDataDto::class.java)
        return jsonVal
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertPresenceQrCode(encryptedBarcodeData: String, eventId: Int) {
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = encryptedBarcodeData.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("${GlobalUtils.apiCommonUrl}/events/entry/$eventId")
            .addHeader("Authorization", GlobalUtils.userInfo.basicAuth)
            .addHeader("Content-Type", "application/octet-stream")
            .post(requestBody)
            .build()

        val response = withContext(Dispatchers.IO) {
            GlobalUtils.httpClient.newCall(request).execute()
        }

        if(response.code != 201){
            throw Exception("Unable to register presence, error ${response.code}")
        }
    }
}