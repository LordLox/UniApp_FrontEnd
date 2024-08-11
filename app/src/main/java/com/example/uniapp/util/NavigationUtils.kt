package com.example.uniapp.util

import android.content.Context
import android.content.Intent
import com.example.uniapp.LoginActivity

object NavigationUtils {
    fun returnToLoginIfNotLogged(context: Context): Unit {
        if (!FileStorageUtils.fileExists(GlobalUtils.userInfoFileName)){
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}