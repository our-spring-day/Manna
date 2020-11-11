package com.manna


import com.google.gson.annotations.SerializedName

data class MyLatLng(
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?
)