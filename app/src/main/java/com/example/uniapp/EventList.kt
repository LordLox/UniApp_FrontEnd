package com.example.uniapp

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uniapp.network.EventListApiService
import kotlinx.coroutines.launch

class EventList : AppCompatActivity() {

    private lateinit var eventListView: ListView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list)

        eventListView = findViewById(R.id.eventlist)

        // Handle back button click
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        loadEvents()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEvents() {
        lifecycleScope.launch {
            try {
                val events = EventListApiService.fetchEvents()

                // Extract event names from EventDto objects
                val eventNames = events.map { it.name }

                // Use the default ArrayAdapter to display the event names
                val adapter = ArrayAdapter(this@EventList, android.R.layout.simple_list_item_1, eventNames)
                eventListView.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(this@EventList, "Failed to load events", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
