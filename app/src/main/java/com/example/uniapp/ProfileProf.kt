package com.example.uniapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Button
import com.example.uniapp.R
import com.example.uniapp.util.NavigationUtils

class ProfileProf : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        NavigationUtils.returnToLoginIfNotLogged(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_prof)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

        // Handle back button click
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        // Handle change password button click
        val changePasswordButton = findViewById<Button>(R.id.change_password_button)
        changePasswordButton.setOnClickListener {
            // Add your change password logic here
        }
    }
}
