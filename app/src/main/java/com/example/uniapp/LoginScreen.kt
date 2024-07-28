package com.example.uniapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.nio.charset.Charset

class LoginScreen : AppCompatActivity() {
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        networkUtils = NetworkUtils(this)
        // Set up button click listeners (optional)
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val url = "https://app.example.com/users/userinfo"
            val authorization = "1234156789"
            val fileName = "encryptedData.dat"
            val isSuccess = networkUtils.fetchAndSaveData(url,authorization,fileName)
            if (isSuccess){
                val file = File("C:\\Programmi visual studio\\Ingegneria\\studentApp\\Files", fileName)
                val encryptedData = file.readBytes()
                val decryptedData = networkUtils.decrypt(encryptedData)
                if (decryptedData != null) {
                    val decryptedString = decryptedData.toString()

                }
            }
        }
    }
}