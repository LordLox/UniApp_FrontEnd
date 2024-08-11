package com.example.uniapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.uniapp.model.UserInfo
import com.example.uniapp.network.LoginApiService
import okhttp3.OkHttpClient
import java.io.File

class GlobalUtils {
    companion object {
        var apiCommonUrl = "https://studentapp.commandware.com"
        val httpClient = OkHttpClient()
        lateinit var applicationPath: File
        var userInfoFileName: String = "user_info"

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getUserInfo(callback: (UserInfo) -> Unit) {
            val encryptedUserInfo = FileStorageUtils.readFromFile(userInfoFileName)
            val result = LoginApiService.decryptUserInfo(encryptedUserInfo)
            callback(result)
        }
    }
}
