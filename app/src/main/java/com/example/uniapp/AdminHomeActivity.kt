package com.example.uniapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.util.NavigationUtils

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NavigationUtils.returnToLoginIfNotLogged(this)
        setContentView(R.layout.admin_home)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        // Set up button click listeners (optional)
        findViewById<Button>(R.id.create_user).setOnClickListener {
            // Handle Read QR Code button click
        }

        findViewById<Button>(R.id.update_user).setOnClickListener {
            // Handle Create com.example.uniapp.Event button click
        }

        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this@AdminHomeActivity, ProfileProfActivity::class.java))
        }
    }
}
