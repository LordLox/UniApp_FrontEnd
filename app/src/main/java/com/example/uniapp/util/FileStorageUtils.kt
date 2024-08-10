package com.example.uniapp.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object FileStorageUtils {

    fun saveToFile(fileName: String, data: String) {
        val file = File(GlobalVariables.applicationPath, fileName)
        FileOutputStream(file).use { output ->
            output.write(data.toByteArray())
        }
    }

    fun readFromFile(fileName: String): String {
        val file = File(GlobalVariables.applicationPath, fileName)
        return file.readText()
    }

    fun fileExists(fileName: String): Boolean {
        val file = File(GlobalVariables.applicationPath, fileName)
        return file.exists()
    }
}
