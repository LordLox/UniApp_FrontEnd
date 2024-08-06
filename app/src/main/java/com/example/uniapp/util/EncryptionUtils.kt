package com.example.uniapp.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {

    @SuppressLint("NewApi")
    fun decrypt(encryptedText: String, aesKey: String): String {
        // Decode the Base64 encoded string
        val fullCipher = Base64.getDecoder().decode(encryptedText)

        // Create an AES cipher instance
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val blockSize = cipher.blockSize

        // Extract the IV and the actual cipher text
        val iv = fullCipher.copyOfRange(0, blockSize)
        val cipherBytes = fullCipher.copyOfRange(blockSize, fullCipher.size)

        // Setup the key and IV
        val keySpec = SecretKeySpec(aesKey.toByteArray(StandardCharsets.UTF_8), "AES")
        val ivSpec = IvParameterSpec(iv)

        // Initialize the cipher for decryption
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        // Perform decryption
        val decryptedBytes = cipher.doFinal(cipherBytes)

        // Convert decrypted bytes to string
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }
}
