package com.example.uniapp

import android.content.Intent
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
import com.example.uniapp.model.EventDto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class EventListActivity : AppCompatActivity() {

    private lateinit var eventListView: ListView
    private lateinit var events: List<EventDto>

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
        val addEventButton = findViewById<FloatingActionButton>(R.id.add_event_button)
        addEventButton.setOnClickListener {
            startActivity(Intent(this@EventListActivity, EventActivity::class.java))
        }

        loadEvents()
        setupEventListView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRestart() {
        super.onRestart()
        loadEvents()
        setupEventListView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEvents() {
        lifecycleScope.launch {
            try {
                events = EventListApiService.fetchEvents()

                // Extract event names from EventDto objects
                val eventNames = events.map { "(${it.type.name}) ${it.name}" }

                // Use the default ArrayAdapter to display the event names
                val adapter = ArrayAdapter(this@EventListActivity, android.R.layout.simple_list_item_1, eventNames)
                eventListView.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(this@EventListActivity, "Failed to load events", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupEventListView() {
        eventListView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedEvent = events[position]
            val intent = Intent(this@EventListActivity, EventActivity::class.java).apply {
                putExtra("EVENT_DTO", selectedEvent)
            }
            startActivity(intent)
            true
        }
        eventListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEvent = events[position]
            val intent = Intent(this@EventListActivity, ReadQrPageActivity::class.java).apply {
                putExtra("EVENT_ID", selectedEvent.id)
            }
            startActivity(intent)
        }
    }
}
