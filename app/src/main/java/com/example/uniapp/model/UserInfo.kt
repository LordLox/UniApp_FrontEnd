package com.example.uniapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.util.Base64

// Data class representing user information
data class UserInfo(
    @SerializedName("Id") var id: Int,
    @SerializedName("Name") var name: String,
    @SerializedName("Badge") var badge: Int,
    @SerializedName("Username") var username: String,
    @SerializedName("Type") var type: UserType,
    @SerializedName("Password") var password: String
) {
    // Property to generate the basic authentication header value
    val basicAuth: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())
    // The above code concatenates the username and password in the "username:password" format,
    // encodes it in Base64, and then prepends "Basic " to conform to the Basic Auth standard.
}

// Enum class representing different user types
enum class UserType {
    Admin,
    Professor,
    Student
}

// Custom deserializer for the UserType enum to handle conversion from JSON
class UserTypeDeserializer : JsonDeserializer<UserType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): UserType {
        // Map the integer from the JSON element to the corresponding UserType enum value
        return when (json?.asInt) {
            0 -> UserType.Admin
            1 -> UserType.Professor
            2 -> UserType.Student
            else -> UserType.Student // Default to Student if the value is unknown or not provided
        }
    }
}

// Function to get a Gson instance with the custom deserializer for UserType
fun userTypeGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(UserType::class.java, UserTypeDeserializer()) // Register the custom deserializer
        .create() // Create and return the Gson instance
}
