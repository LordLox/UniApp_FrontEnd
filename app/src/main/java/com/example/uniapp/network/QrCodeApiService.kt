package com.example.uniapp.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
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
}