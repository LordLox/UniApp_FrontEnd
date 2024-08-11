package com.example.uniapp.util

import android.content.Context
import com.example.uniapp.R

object SpinnerUtils {
    fun getSpinnerPosition(context: Context, category: String): Int {
        val categories = context.resources.getStringArray(R.array.event_categories)
        for (i in categories.indices) {
            if (categories[i].equals(category, ignoreCase = true)) {
                return i
            }
        }
        return 0 // Default position
    }
}
