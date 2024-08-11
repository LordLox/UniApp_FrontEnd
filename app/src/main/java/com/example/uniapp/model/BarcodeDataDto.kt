package com.example.uniapp.model

import com.google.gson.annotations.SerializedName

data class BarcodeDataDto(
    @SerializedName("Id") val id: Int,
    @SerializedName("Username") val username: String = "",
    @SerializedName("Name") val name: String = "",
    @SerializedName("Badge") val badge: Int,
    @SerializedName("Epoch") val epoch: Long
)
