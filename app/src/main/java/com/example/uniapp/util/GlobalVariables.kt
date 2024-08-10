package com.example.uniapp.util

import okhttp3.OkHttpClient
import java.io.File

class GlobalVariables {
    companion object {
        var apiCommonUrl = "https://studentapp.commandware.com"
        val httpClient = OkHttpClient()
        lateinit var applicationPath: File
        var userInfoFileName: String = "user_info"
    }
}
