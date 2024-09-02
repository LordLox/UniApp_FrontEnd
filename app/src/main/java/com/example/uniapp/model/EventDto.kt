package com.example.uniapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class EventDto(
    @SerializedName("name") var name: String = "",

    @SerializedName("userId") var userId: Int,

    @SerializedName("type") var type: EventType,

    @SerializedName("id") var id: Int
) : Parcelable { // Implements Parcelable to allow EventDto objects to be passed between Android components

    // Constructor to create an EventDto object from a Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",

        parcel.readInt(),

        EventType.valueOf(parcel.readString() ?: EventType.Lesson.name),

        parcel.readInt()
    )

    // Method to write the EventDto object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(userId)
        parcel.writeString(type.name) // Writing the EventType as a string
        parcel.writeInt(id)
    }

    // Method required by Parcelable, usually returns 0 (or special flags)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object that serves as a CREATOR, responsible for creating EventDto instances from a Parcel
    companion object CREATOR : Parcelable.Creator<EventDto> {
        override fun createFromParcel(parcel: Parcel): EventDto {
            return EventDto(parcel) // Calls the constructor that takes a Parcel
        }

        override fun newArray(size: Int): Array<EventDto?> {
            return arrayOfNulls(size) // Creates an array of EventDto objects
        }
    }
}

// Enum class representing different event types with an associated integer value and display name
enum class EventType(val value: Int, private val displayName: String) {
    Lesson(0, "Lesson"),
    Conference(1, "Conference"),
    Lab(2, "Lab");

    // Override toString() to return the display name instead of the enum name
    override fun toString(): String {
        return displayName
    }
}

// Custom deserializer for the EventType enum to handle conversion from JSON
class EventTypeDeserializer : JsonDeserializer<EventType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EventType {
        // Retrieve the integer value from the JSON element
        val eventTypeInt = json?.asInt ?: throw JsonParseException("Invalid event type: null")

        // Map the integer value to the corresponding EventType enum value
        return when (eventTypeInt) {
            EventType.Lesson.value -> EventType.Lesson
            EventType.Conference.value -> EventType.Conference
            EventType.Lab.value -> EventType.Lab
            else -> throw JsonParseException("Unknown event type: $eventTypeInt") // Handle unknown values
        }
    }
}

// Custom serializer for the EventType enum to handle conversion to JSON
class EventTypeSerializer : JsonSerializer<EventType> {
    override fun serialize(src: EventType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        // Convert the EventType enum value to a JSON primitive (integer)
        return JsonPrimitive(src?.value)
    }
}

// Function to get a Gson instance with the custom deserializer and serializer for EventType
fun eventTypeGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(EventType::class.java, EventTypeDeserializer()) // Register the deserializer
        .registerTypeAdapter(EventType::class.java, EventTypeSerializer())   // Register the serializer
        .create() // Create and return the Gson instance
}
