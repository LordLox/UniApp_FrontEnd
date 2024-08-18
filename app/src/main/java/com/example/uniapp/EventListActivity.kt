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

    private lateinit var eventListView: ListView
    private lateinit var events: List<EventDto>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

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

        val downloadEvents = findViewById<AppCompatButton>(R.id.download_event)
        downloadEvents.setOnClickListener {
            showDateRangePicker()
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

    private fun showDateRangePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        dateRangePicker.show(supportFragmentManager, "date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            // Selection contains the Pair of selected start and end dates in milliseconds
            val startTimestamp = selection.first / 1000L // Convert to seconds
            val endTimestamp = (selection.second / 1000L) + 86400 // Convert to seconds and add 1 day

            lifecycleScope.launch {
                try {
                    val history = EventApiService.fetchEventHistory(startTimestamp, endTimestamp)
                    if (history != null) {
                        saveHistoryToFile(history)
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

    private fun saveHistoryToFile(data: String) {
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Get the current date and time
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // Create a unique file name by appending the current date and time
        val fileName = "event_history_$currentDate.csv"

        FileStorageUtils.saveToFile(downloadsFolder, fileName, data)
    }
}
