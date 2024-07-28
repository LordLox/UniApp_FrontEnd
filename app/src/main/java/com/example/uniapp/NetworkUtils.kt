package com.example.uniapp

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import java.nio.charset.StandardCharsets

class NetworkUtils(private val context: Context) {

    private val client = OkHttpClient()

    fun fetchAndSaveData(url: String, authorization: String, fileName: String): Boolean {
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Basic $authorization")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.bytes()
                if (responseData != null) {
                    saveToFile(responseData, fileName)
                    return true
                }
            }
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun saveToFile(data: ByteArray, fileName: String) {
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { fos ->
            fos.write(data)
        }
    }

    fun decrypt(encryptedText: ByteArray): String {
        val aesKey = "qwr{@^h`h&_`50/ja9!'dcmh3!uw<&=?"
        val fullCipher = Base64.decode(encryptedText, Base64.DEFAULT)
        val aes = Cipher.getInstance("AES/CBC/PKCS5PADDING")

        val iv = ByteArray(aes.blockSize / 8)
        val cipher = ByteArray(fullCipher.size - iv.size)

        System.arraycopy(fullCipher, 0, iv, 0, iv.size)
        System.arraycopy(fullCipher, iv.size, cipher, 0, cipher.size)

        val keyBytes = aesKey.toByteArray(StandardCharsets.UTF_8)
        val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")

        aes.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decryptedText = aes.doFinal(cipher)
        return String(decryptedText, StandardCharsets.UTF_8)
    }
}
