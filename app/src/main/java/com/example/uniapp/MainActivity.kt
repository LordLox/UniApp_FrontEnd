package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uniapp.model.UserInfo
import com.example.uniapp.model.UserType
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.launch
import java.util.Base64

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalUtils.applicationPath = this.filesDir
        super.onCreate(savedInstanceState)

        // Go to specific page only if user already logged in
        if(FileStorageUtils.fileExists(GlobalUtils.userInfoFileName))
        {
            lifecycleScope.launch {
                GlobalUtils.getUserInfo { userInfo ->
                    GlobalUtils.userInfo = userInfo
                    routeToPageBasedOnUser()
                }
            }
        }
        else
        {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    private fun routeToPageBasedOnUser(){
        val intent = when (GlobalUtils.userInfo.type) {
            UserType.Admin -> Intent(this@MainActivity, AdminHome::class.java)
            UserType.Professor -> Intent(this@MainActivity, HomepageProfessor::class.java)
            UserType.Student -> Intent(this@MainActivity, QrCodePage::class.java)
        }
        startActivity(intent)
    }
}