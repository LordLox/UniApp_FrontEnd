package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.uniapp.network.LoginApiService
import com.example.uniapp.util.EncryptionUtils
import com.example.uniapp.util.FileStorageUtils
import com.example.uniapp.util.GlobalVariables

class LoginActivity : AppCompatActivity() {

    private val apiUrl = "${GlobalVariables.apiCommonUrl}users/userinfo"
    private val aesKey = "${GlobalVariables.AESKey}" // Replace with your actual key
    private lateinit var loginApiService: LoginApiService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        loginApiService = LoginApiService(apiUrl)

        val usernameEditText = findViewById<EditText>(R.id.usernameField)
        val passwordEditText = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(username, password)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loginUser(username: String, password: String) {
        loginApiService.loginUser(username, password) { success, encryptedUserInfo ->
            if (success && encryptedUserInfo != null) {
                FileStorageUtils.saveToFile(this, "user_info.txt", encryptedUserInfo)
                val intent = Intent(this, HomepageProfessor::class.java)
                startActivity(intent)
                decryptUserInfo(encryptedUserInfo)
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decryptUserInfo(encryptedUserInfo: String) {
        val decryptedUserInfo = EncryptionUtils.decrypt(encryptedUserInfo, aesKey)
        runOnUiThread {
            if (decryptedUserInfo != null) {
                Toast.makeText(this, "Decrypted User Info: $decryptedUserInfo", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
