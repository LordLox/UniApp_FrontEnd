package com.example.uniapp

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.model.EventDto
import com.example.uniapp.model.EventType
import com.example.uniapp.network.EventApiService
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Activity class for creating, updating, and deleting events
class EventActivity : AppCompatActivity() {

    // UI components
    private lateinit var eventTypeSpinner: Spinner
    private lateinit var eventNameEditText: EditText
    private lateinit var confirmButton: AppCompatButton
    private lateinit var deleteButton: AppCompatButton
    private var eventDto: EventDto? = null // Holds the event data object

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide the default toolbar title

        // Initialize UI components
        eventTypeSpinner = findViewById(R.id.spinner)
        eventNameEditText = findViewById(R.id.eventField)
        confirmButton = findViewById(R.id.confirmEvent)
        deleteButton = findViewById(R.id.deleteEvent)

        // Handle back button click
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to return to the previous screen
        }

        // Retrieve the event ID from the Intent (if provided)
        eventDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EVENT_DTO", EventDto::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EVENT_DTO")
        }

        // Show the delete button only if updating an existing event
        if (eventDto != null) {
            deleteButton.visibility = Button.VISIBLE
        } else {
            deleteButton.visibility = Button.GONE
        }

        // Populate the event type spinner with enum values
        populateEventTypeSpinner()

        // If editing an existing event, load its details into the UI
        eventDto?.let {
            loadEventDetails(it)
        }

        // Set up button listeners for confirm and delete actions
        setupButtons()
    }

    // Populates the spinner with event types from the EventType enum
    private fun populateEventTypeSpinner() {
        val eventTypes = EventType.entries.toTypedArray() // Get all enum values
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes) // Create adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Set dropdown layout
        eventTypeSpinner.adapter = adapter // Set adapter to spinner
    }

    @RequiresApi(Build.VERSION_CODES.O)
    // Loads existing event details into the form fields
    private fun loadEventDetails(event: EventDto) {
        eventNameEditText.setText(event.name) // Set event name in EditText
        eventTypeSpinner.setSelection(event.type.ordinal) // Set event type in Spinner
    }

    @RequiresApi(Build.VERSION_CODES.O)
    // Sets up the button listeners for creating/updating and deleting events
    private fun setupButtons() {
        // Listener for the confirm button
        confirmButton.setOnClickListener {
            val name = eventNameEditText.text.toString() // Get event name from input
            val eventType = eventTypeSpinner.selectedItem as EventType // Get selected event type

            // Check if the event name is not empty
            if (name.isBlank()) {
                Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Coroutine for handling the event creation/updating in the background
            CoroutineScope(Dispatchers.Main).launch {
                if (eventDto == null) {
                    // Create a new event if eventDto is null
                    val newEvent = EventDto(
                        name = name,
                        userId = GlobalUtils.userInfo.id,
                        type = eventType,
                        id = 0 // id = 0 is ignored by the server and will be replaced with a generated ID
                    )
                    val newEventId = withContext(Dispatchers.IO) {
                        EventApiService.createEvent(newEvent) // Call the API to create the event
                    }
                    newEventId?.let {
                        Toast.makeText(this@EventActivity, "Event created successfully", Toast.LENGTH_SHORT).show()
                        newEvent.id = it // Update the event ID with the newly generated ID
                        eventDto = newEvent // Set the eventDto to the newly created event
                        finish() // Close the activity after successful creation
                    } ?: run {
                        Toast.makeText(this@EventActivity, "Failed to create event", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Update the existing event if eventDto is not null
                    val updatedEvent = eventDto!!.copy(name = name, type = eventType)
                    withContext(Dispatchers.IO) {
                        try {
                            EventApiService.updateEvent(updatedEvent) // Call the API to update the event
                            runOnUiThread {
                                Toast.makeText(this@EventActivity, "Event updated successfully", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@EventActivity, "Error updating Event", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        // Listener for the delete button
        deleteButton.setOnClickListener {
            eventDto?.let { event ->
                // Coroutine for handling the event deletion in the background
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        EventApiService.deleteEvent(event.id) // Call the API to delete the event
                    }
                    Toast.makeText(this@EventActivity, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after successful deletion
                }
            } ?: run {
                Toast.makeText(this, "No event to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
