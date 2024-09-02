package com.example.uniapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.util.GlobalUtils
import com.example.uniapp.util.NavigationUtils

class HomepageProfessorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Ensure the user is logged in before proceeding. If not, redirect to the login screen.
        NavigationUtils.returnToLoginIfNotLogged(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_professor) // Inflate the layout for the professor's homepage

        // Check if the app has permission to use the camera. If not, request permission.
        GlobalUtils.checkCameraPermission(this)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide the default title to customize the toolbar appearance

        // Set up the button click listeners for the different actions on the homepage

        // Listener for the "Create Event" button. Navigates to the EventActivity to create a new event.
        findViewById<Button>(R.id.create_event_button).setOnClickListener {
            startActivity(Intent(this@HomepageProfessorActivity, EventActivity::class.java))
        }

        // Listener for the "Event List" button. Opens the EventListActivity to view a list of events.
        findViewById<Button>(R.id.event_list_button).setOnClickListener {
            startActivity(Intent(this@HomepageProfessorActivity, EventListActivity::class.java))
        }

        // Listener for the profile icon. Opens the ProfileProfActivity to view or edit the professor's profile.
        findViewById<ImageView>(R.id.profile_icon).setOnClickListener {
            startActivity(Intent(this@HomepageProfessorActivity, ProfileProfActivity::class.java))
        }
    }
}
