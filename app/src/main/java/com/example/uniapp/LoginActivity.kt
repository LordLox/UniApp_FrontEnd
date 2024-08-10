package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uniapp.model.UserInfo
import com.example.uniapp.model.UserType
import com.example.uniapp.network.LoginApiService
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalVariables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class LoginActivity : AppCompatActivity() {

    private var loginApiService: LoginApiService = LoginApiService()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalVariables.applicationPath = this.filesDir
        super.onCreate(savedInstanceState)

        // Go to specific page only if user already logged in
        if(FileStorageUtils.fileExists(GlobalVariables.userInfoFileName))
        {
            getUserInfo { userInfo ->
                routeToPageBasedOnUser(userInfo)
            }
        }

        setContentView(R.layout.login_screen)

        val usernameEditText = findViewById<EditText>(R.id.usernameField)
        val passwordEditText = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                loginUser(username, password)
            }
        }
    }

    // Function to perform the suspend operation and invoke the callback with the result
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUserInfo(callback: (UserInfo) -> Unit) {
        lifecycleScope.launch {
            val encryptedUserInfo = FileStorageUtils.readFromFile(GlobalVariables.userInfoFileName)
            val result = loginApiService.decryptUserInfo(encryptedUserInfo)
            callback(result)
        }
    }

    private fun routeToPageBasedOnUser(userInfo: UserInfo){
        val intent = when (userInfo.type) {
            UserType.Admin -> Intent(this@LoginActivity, AdminHome::class.java)
            UserType.Professor -> Intent(this@LoginActivity, HomepageProfessor::class.java)
            UserType.Student -> Intent(this@LoginActivity, QrCodePage::class.java)
        }
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loginUser(username: String, password: String) {
        try {
            val userInfo = loginApiService.loginUser(username, password)
            withContext(Dispatchers.Main) {
                routeToPageBasedOnUser(userInfo)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
