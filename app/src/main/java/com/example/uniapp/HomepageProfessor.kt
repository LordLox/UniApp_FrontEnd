package com.example.uniapp
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HomepageProfessor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_professor)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up button click listeners (optional)
        findViewById<Button>(R.id.read_qr_code_button).setOnClickListener {
            // Handle Read QR Code button click
        }

        findViewById<Button>(R.id.create_event_button).setOnClickListener {
            // Handle Create Event button click
        }

        findViewById<Button>(R.id.event_list_button).setOnClickListener {
            // Handle Event List button click
        }
    }
}
