package com.example.uniapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.example.uniapp.network.EventApiService
import com.example.uniapp.util.NavigationUtils
import com.example.uniapp.util.SpinnerUtils
import org.json.JSONObject

class Event : AppCompatActivity() {

    private lateinit var eventField: EditText
    private lateinit var spinner: Spinner
    private lateinit var okButton: AppCompatButton
    private var isUpdate = false
    private var eventId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        NavigationUtils.returnToLoginIfNotLogged(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        eventField = findViewById(R.id.eventField)
        spinner = findViewById(R.id.spinner)
        okButton = findViewById(R.id.confirmEvent)

        // Set up the spinner
        val adapter = ArrayAdapter.createFromResource(this, R.array.event_categories, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Check if this is an update or a new event
        val intent = intent
        if (intent != null && intent.hasExtra("eventId")) {
            isUpdate = true
            eventId = intent.getIntExtra("eventId", -1)
            // Fetch event details and populate fields
            fetchEventDetails(eventId)
        }

        okButton.setOnClickListener {
            val eventName = eventField.text.toString()
            val eventCategory = spinner.selectedItem.toString()

            if (TextUtils.isEmpty(eventName)) {
                Toast.makeText(this, "Please enter the event name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val eventDetails = JSONObject().apply {
                    put("name", eventName)
                    put("category", eventCategory)
                }

                if (isUpdate) {
                    updateEvent(eventDetails)
                } else {
                    createEvent(eventDetails)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchEventDetails(eventId: Int) {
        EventApiService.fetchEventDetails(eventId,
            { response ->
                val name = response.getString("name")
                val category = response.getString("category")

                eventField.setText(name)
                val spinnerPosition = SpinnerUtils.getSpinnerPosition(this, category)
                spinner.setSelection(spinnerPosition)
            },
            { error ->
                Toast.makeText(this, "Failed to fetch event details", Toast.LENGTH_SHORT).show()
            })
    }

    private fun createEvent(eventDetails: JSONObject) {
        EventApiService.createEvent(eventDetails,
            {
                Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show()
            })
    }

    private fun updateEvent(eventDetails: JSONObject) {
        EventApiService.updateEvent(eventDetails,
            {
                Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show()
            })
    }
}
