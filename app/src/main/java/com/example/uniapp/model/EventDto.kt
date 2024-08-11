package com.example.uniapp.model

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class EventDto(
    @SerializedName("name") var name: String = "",
    @SerializedName("user_id") var userId: Int,
    @SerializedName("type") var type: EventType
)

enum class EventType {
    Lesson,
    Conference,
    Lab
}

// Custom deserializer for EventType
class EventTypeDeserializer : JsonDeserializer<EventType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EventType {
        return when (json?.asString?.lowercase()) {
            "lesson" -> EventType.Lesson
            "conference" -> EventType.Conference
            "lab" -> EventType.Lab
            else -> throw JsonParseException("Unknown event type: ${json?.asString}")
        }
    }
}

// Function to get a Gson instance with the custom deserializer
fun eventTypeGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(EventType::class.java, EventTypeDeserializer())
        .create()
}
