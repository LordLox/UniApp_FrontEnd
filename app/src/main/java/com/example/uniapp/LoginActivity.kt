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
        setContentView(R.layout.login_screen)  // Set the layout for the login screen

        // Get references to the username and password input fields
        val usernameEditText = findViewById<EditText>(R.id.usernameField)
        val passwordEditText = findViewById<EditText>(R.id.passwordField)

        // Get reference to the login button
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Set a click listener for the login button
        loginButton.setOnClickListener {
            // Get the input from username and password fields
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Use a Coroutine to execute the login process on the main thread
            CoroutineScope(Dispatchers.Main).launch {
                loginUser(username, password)
            }
        }
    }

    // Function to handle the user login process asynchronously
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loginUser(username: String, password: String) {
        try {
            // Make a network request to log in the user and store the user info in the global variable
            GlobalUtils.userInfo = LoginApiService.loginUser(username, password)

            // Switch back to the main thread to start a new activity after successful login
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java)) // Navigate to MainActivity
            }
        } catch (e: Exception) {
            // In case of login failure, display a toast message on the main thread
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
