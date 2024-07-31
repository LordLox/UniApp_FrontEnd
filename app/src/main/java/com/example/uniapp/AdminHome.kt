package com.example.uniapp
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AdminHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_home)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up button click listeners (optional)
        findViewById<Button>(R.id.create_user).setOnClickListener {
            // Handle Read QR Code button click
        }

        findViewById<Button>(R.id.update_user).setOnClickListener {
            // Handle Create com.example.uniapp.Event button click
        }
    }
}