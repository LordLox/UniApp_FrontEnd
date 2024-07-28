package com.example.uniapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class EventList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list)

        // use arrayadapter and define an array
        val arrayAdapter: ArrayAdapter<*>
        val users = arrayOf(
            "Virat Kohli", "Rohit Sharma", "Steve Smith",
            "Kane Williamson", "Ross Taylor"
        )

        // access the listView from xml file
        var mListView = findViewById<ListView>(R.id.eventlist)
        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, users
        )
        mListView.adapter = arrayAdapter
    }
}