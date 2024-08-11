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

data class UserInfo(
    @SerializedName("Id") var id: Int,
    @SerializedName("Name") var name: String,
    @SerializedName("Badge") var badge: Int,
    @SerializedName("Username") var username: String,
    @SerializedName("Type") var type: UserType,
    @SerializedName("Password") var password: String
) {
    val basicAuth: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())
}


enum class UserType {
    Admin,
    Professor,
    Student
}

// Custom deserializer for UserType
class UserTypeDeserializer : JsonDeserializer<UserType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): UserType {
        return when (json?.asInt) {
            0 -> UserType.Admin
            1 -> UserType.Professor
            2 -> UserType.Student
            else -> UserType.Student // Default to Student or handle as needed
        }
    }
}

// Function to get a Gson instance with the custom deserializer
fun userTypeGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(UserType::class.java, UserTypeDeserializer())
        .create()
}