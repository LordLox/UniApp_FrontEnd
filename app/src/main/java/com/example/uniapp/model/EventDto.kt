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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        EventType.valueOf(parcel.readString() ?: EventType.Lesson.name),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(userId)
        parcel.writeString(type.name)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventDto> {
        override fun createFromParcel(parcel: Parcel): EventDto {
            return EventDto(parcel)
        }

        override fun newArray(size: Int): Array<EventDto?> {
            return arrayOfNulls(size)
        }
    }
}

enum class EventType(val value: Int, private val displayName: String) {
    Lesson(0, "Lesson"),
    Conference(1, "Conference"),
    Lab(2, "Lab");

    override fun toString(): String {
        return displayName
    }
}


// Custom deserializer for EventType
class EventTypeDeserializer : JsonDeserializer<EventType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EventType {
        val eventTypeInt = json?.asInt ?: throw JsonParseException("Invalid event type: null")
        return when (eventTypeInt) {
            EventType.Lesson.value -> EventType.Lesson
            EventType.Conference.value -> EventType.Conference
            EventType.Lab.value -> EventType.Lab
            else -> throw JsonParseException("Unknown event type: $eventTypeInt")
        }
    }
}

class EventTypeSerializer : JsonSerializer<EventType> {
    override fun serialize(src: EventType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.value)
    }
}


// Function to get a Gson instance with the custom deserializer
fun eventTypeGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(EventType::class.java, EventTypeDeserializer())
        .registerTypeAdapter(EventType::class.java, EventTypeSerializer())
        .create()
}
