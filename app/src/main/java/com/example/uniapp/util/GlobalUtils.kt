package com.example.uniapp.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uniapp.model.UserInfo
import com.example.uniapp.network.LoginApiService
import okhttp3.OkHttpClient
import java.io.File

class GlobalUtils {
    companion object {
        var apiCommonUrl = "https://backend.uniscanapp.it"
        val httpClient = OkHttpClient()
        lateinit var applicationPath: File
        var userInfoFileName: String = "user_info"
        lateinit var userInfo: UserInfo

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getUserInfo(callback: (UserInfo) -> Unit) {
            val encryptedUserInfo = FileStorageUtils.readFromFile(applicationPath, userInfoFileName)
            val result = LoginApiService.decryptUserInfo(encryptedUserInfo)
            callback(result)
        }

        fun checkCameraPermission(activity: Activity) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CAMERA), 1001)
            }
        }
    }
}