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
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loginUser(username: String, password: String) {
        try {
            GlobalUtils.userInfo = LoginApiService.loginUser(username, password)
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
