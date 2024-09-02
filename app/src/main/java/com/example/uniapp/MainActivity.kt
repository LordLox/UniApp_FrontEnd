package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.uniapp.model.UserType
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set the app to always use light mode (disable dark mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the global path for the application's files directory
        GlobalUtils.applicationPath = this.filesDir

        // Call the superclass's onCreate method to perform standard initialization
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in by checking if the user info file exists
        if (FileStorageUtils.fileExists(GlobalUtils.applicationPath, GlobalUtils.userInfoFileName)) {
            // If the file exists, retrieve the user information and route to the appropriate page
            lifecycleScope.launch {
                GlobalUtils.getUserInfo { userInfo ->
                    // Save the retrieved user info in the global variable
                    GlobalUtils.userInfo = userInfo
                    // Navigate to the appropriate page based on the user's role
                    routeToPageBasedOnUser()
                }
            }
        } else {
            // If the user is not logged in, navigate to the LoginActivity
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    // Function to route the user to the correct homepage based on their role
    private fun routeToPageBasedOnUser() {
        // Determine the user's role and create an intent to navigate to the appropriate activity
        val intent = when (GlobalUtils.userInfo.type) {
            UserType.Admin -> Intent(this@MainActivity, AdminHomeActivity::class.java)
            UserType.Professor -> Intent(this@MainActivity, HomepageProfessorActivity::class.java)
            UserType.Student -> Intent(this@MainActivity, QrCodePageActivity::class.java)
        }
        // Start the selected activity
        startActivity(intent)
    }
}
