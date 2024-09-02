package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.uniapp.network.EventListApiService
import com.example.uniapp.model.EventDto
import com.example.uniapp.network.EventApiService
import com.example.uniapp.util.FileStorageUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EventListActivity : AppCompatActivity() {

    // UI components
    private lateinit var eventListView: ListView
    private lateinit var events: List<EventDto>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide the default title

        // Initialize the ListView for displaying events
        eventListView = findViewById(R.id.eventlist)

        // Set up back button to finish the activity when clicked
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        // Set up the add event button to open the EventActivity for creating a new event
        val addEventButton = findViewById<FloatingActionButton>(R.id.add_event_button)
        addEventButton.setOnClickListener {
            startActivity(Intent(this@EventListActivity, EventActivity::class.java))
        }

        // Set up the download events button to show the date range picker
        val downloadEvents = findViewById<AppCompatButton>(R.id.download_event)
        downloadEvents.setOnClickListener {
            showDateRangePicker()
        }

        // Load and display the list of events
        loadEvents()
        setupEventListView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRestart() {
        super.onRestart()
        // Reload events and refresh the ListView when the activity is restarted
        loadEvents()
        setupEventListView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEvents() {
        // Use a coroutine to load events in the background
        lifecycleScope.launch {
            try {
                events = EventListApiService.fetchEvents() // Fetch events from the API

                // Extract event names from the EventDto objects and format them
                val eventNames = events.map { "(${it.type.name}) ${it.name}" }

                // Use an ArrayAdapter to display the event names in the ListView
                val adapter = ArrayAdapter(this@EventListActivity, android.R.layout.simple_list_item_1, eventNames)
                eventListView.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(this@EventListActivity, "Failed to load events", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupEventListView() {
        // Set up long click listener for each item in the ListView to allow editing the event
        eventListView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedEvent = events[position]
            val intent = Intent(this@EventListActivity, EventActivity::class.java).apply {
                putExtra("EVENT_DTO", selectedEvent) // Pass the selected event to the EventActivity
            }
            startActivity(intent)
            true
        }

        // Set up click listener for each item in the ListView to open the ReadQrPageActivity
        eventListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEvent = events[position]
            val intent = Intent(this@EventListActivity, ReadQrPageActivity::class.java).apply {
                putExtra("EVENT_ID", selectedEvent.id) // Pass the selected event ID to the ReadQrPageActivity
            }
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDateRangePicker() {
        // Set up constraints for the date picker (optional)
        val constraintsBuilder = CalendarConstraints.Builder()

        // Build and show the MaterialDatePicker for selecting a date range
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range") // Set the title of the picker
            .setCalendarConstraints(constraintsBuilder.build()) // Apply any constraints
            .build()

        // Show the date range picker dialog
        dateRangePicker.show(supportFragmentManager, "date_range_picker")

        // Handle the positive button click (date range selected)
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            // The selection contains a Pair of start and end dates in milliseconds
            val startTimestamp = selection.first / 1000L // Convert to seconds
            val endTimestamp = (selection.second / 1000L) + 86400 // Convert to seconds and add 1 day

            // Fetch and save event history based on the selected date range
            lifecycleScope.launch {
                try {
                    val history = EventApiService.fetchEventHistory(startTimestamp, endTimestamp)
                    if (history != null) {
                        saveHistoryToFile(history) // Save the history data to a file
                        Toast.makeText(this@EventListActivity, "Event history saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EventListActivity, "No history data from that range", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@EventListActivity, "Error fetching or saving history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Saves the fetched history data to a file in the Downloads folder
    private fun saveHistoryToFile(data: String) {
        // Get the path to the Downloads folder
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Get the current date and time for a unique file name
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // Create a unique file name by appending the current date and time
        val fileName = "event_history_$currentDate.csv"

        // Save the data to the file
        FileStorageUtils.saveToFile(downloadsFolder, fileName, data)
    }
}
