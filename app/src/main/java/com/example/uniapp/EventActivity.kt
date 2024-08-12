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
import com.example.uniapp.model.EventDto
import com.example.uniapp.model.EventType
import com.example.uniapp.network.EventApiService
import com.example.uniapp.util.GlobalUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventActivity : AppCompatActivity() {

    private lateinit var eventTypeSpinner: Spinner
    private lateinit var eventNameEditText: EditText
    private lateinit var confirmButton: AppCompatButton
    private lateinit var deleteButton: AppCompatButton
    private var eventDto: EventDto? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // Initialize views
        eventTypeSpinner = findViewById(R.id.spinner)
        eventNameEditText = findViewById(R.id.eventField)
        confirmButton = findViewById(R.id.confirmEvent)
        deleteButton = findViewById(R.id.deleteEvent)

        // Handle back button click
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Finish the activity to go back
        }

        // Retrieve the event ID from the Intent (if any)
        eventDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EVENT_DTO", EventDto::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EVENT_DTO")
        }

        // Populate Spinner with EventType enum values
        populateEventTypeSpinner()

        // Load event details if updating or deleting
        eventDto?.let {
            loadEventDetails(it)
        }

        setupButtons()
    }

    private fun populateEventTypeSpinner() {
        val eventTypes = EventType.entries.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        eventTypeSpinner.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEventDetails(event: EventDto) {
        eventNameEditText.setText(event.name)
        eventTypeSpinner.setSelection(event.type.ordinal)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupButtons() {
        confirmButton.setOnClickListener {
            val name = eventNameEditText.text.toString()
            val eventType = eventTypeSpinner.selectedItem as EventType

            if (name.isBlank()) {
                Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                if (eventDto == null) {
                    // Create a new event
                    val newEvent = EventDto(name, userId = GlobalUtils.userInfo.id, type = eventType, id = 0) // id = 0 is ignored by the server
                    val newEventId = withContext(Dispatchers.IO) {
                        EventApiService.createEvent(newEvent)
                    }
                    newEventId?.let {
                        Toast.makeText(this@EventActivity, "Event created succesfully", Toast.LENGTH_SHORT).show()
                        newEvent.id = it
                        eventDto = newEvent
                    } ?: run {
                        Toast.makeText(this@EventActivity, "Failed to create event", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Update existing event
                    val updatedEvent = eventDto!!.copy(name = name, type = eventType)
                    withContext(Dispatchers.IO) {
                        try {
                            EventApiService.updateEvent(updatedEvent)
                            runOnUiThread {
                                Toast.makeText(this@EventActivity, "Event updated successfully", Toast.LENGTH_SHORT).show()
                                //finish() // Close activity after successful update
                            }
                        }
                        catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@EventActivity, "Error upadting Event", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        deleteButton.setOnClickListener {
            eventDto?.let { event ->
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        EventApiService.deleteEvent(event.id)
                    }
                    Toast.makeText(this@EventActivity, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close activity after successful deletion
                }
            } ?: run {
                Toast.makeText(this, "No event to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
