package com.example.uniapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

class EventAdapter(private val context: Context, private val events: JSONArray) : BaseAdapter() {

    override fun getCount(): Int {
        return events.length()
    }

    override fun getItem(position: Int): JSONObject {
        return events.getJSONObject(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_list, parent, false)

        val eventNameTextView: TextView = view.findViewById(R.id.eventlist)
        val event = getItem(position)

        eventNameTextView.text = event.getString("name")

        return view
    }
}
