package com.example.uniapp.util

import java.io.File
import java.io.FileOutputStream

object FileStorageUtils {

    fun saveToFile(filePath: File, fileName: String, data: String) {
        val file = File(filePath, fileName)
        FileOutputStream(file).use { output ->
            output.write(data.toByteArray())
        }
    }

    fun readFromFile(filePath: File, fileName: String): String {
        val file = File(filePath, fileName)
        return file.readText()
    }

    fun fileExists(filePath: File, fileName: String): Boolean {
        val file = File(filePath, fileName)
        return file.exists()
    }

    fun deleteFile(filePath: File, fileName: String) {
        val file = File(filePath, fileName)
        file.delete()
    }
}
