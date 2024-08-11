package com.example.uniapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import com.example.uniapp.network.EventListApiService
import com.example.uniapp.util.GlobalUtils
import com.example.uniapp.util.NavigationUtils
import kotlinx.coroutines.launch

class EventList : AppCompatActivity() {

    private lateinit var eventListView: ListView
    private lateinit var updateEventButton: AppCompatButton
    private lateinit var downloadEventButton: AppCompatButton
    private lateinit var addEventButton: FloatingActionButton
    private var selectedEvent: JSONObject? = null
    private lateinit var eventAdapter: EventAdapter // Custom adapter for the ListView
    private val apiUrl = "${GlobalUtils.apiCommonUrl}/events/personal/1"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        NavigationUtils.returnToLoginIfNotLogged(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list)

        eventListView = findViewById(R.id.eventlist)
        updateEventButton = findViewById(R.id.update_event)
        downloadEventButton = findViewById(R.id.download_event)
        addEventButton = findViewById(R.id.add_event_button)

        loadEvents()

        eventListView.setOnItemClickListener { _, _, position, _ ->
            selectedEvent = eventAdapter.getItem(position)
            Toast.makeText(this, "Selected: ${selectedEvent?.getString("name")}", Toast.LENGTH_SHORT).show()
            // Here you might want to show the delete button
        }

        updateEventButton.setOnClickListener {
            selectedEvent?.let {
                val intent = Intent(this, Event::class.java)
                intent.putExtra("event", it.toString())
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Please select an event to update", Toast.LENGTH_SHORT).show()
            }
        }

        downloadEventButton.setOnClickListener {
            selectedEvent?.let {
                downloadEvent(it)
            } ?: run {
                Toast.makeText(this, "Please select an event to download", Toast.LENGTH_SHORT).show()
            }
        }

        addEventButton.setOnClickListener {
            val intent = Intent(this, Event::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEvents() {
        lifecycleScope.launch {
            val events = EventListApiService.getEventsFromAPI()
            eventAdapter = EventAdapter(this@EventList, events)
            eventListView.adapter = eventAdapter
        }
        // Make an API call to load events (Assume you have a function to do that)
         // This should return a JSONArray
    }

    private fun downloadEvent(event: JSONObject) {
        try {
            val fileName = "${event.getString("name")}.txt"
            val file = File(getExternalFilesDir(null), fileName)
            val fos = FileOutputStream(file)
            val osw = OutputStreamWriter(fos)
            osw.write(event.toString())
            osw.close()
            fos.close()
            Toast.makeText(this, "Event downloaded to $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to download event", Toast.LENGTH_SHORT).show()
        }
    }

    // Additional functions like deleteEvent and other API interactions can be added here
}
