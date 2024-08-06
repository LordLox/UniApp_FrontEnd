package com.example.uniapp.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object FileStorageUtils {

    fun saveToFile(context: Context, fileName: String, data: String) {
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(data.toByteArray())
        }
    }

    fun readFromFile(context: Context, fileName: String): String? {
        return try {
            val file = File(context.filesDir, fileName)
            file.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
