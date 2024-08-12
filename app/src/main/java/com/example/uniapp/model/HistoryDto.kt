package com.example.uniapp.model

import com.google.gson.annotations.SerializedName

data class HistoryDto(
    @SerializedName("eventName") var eventName: String = "",
    @SerializedName("userBirthName") var userBirthName: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userBadge") var userBadge: Int,
    @SerializedName("eventEntryDate") var eventEntryDate: String = ""
)

// No enums were specified in the original class, so no custom deserializer is required.
