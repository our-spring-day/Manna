package com.manna

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("location")
    val latLng: MyLatLng?,
    @SerializedName("sender")
    val sender: Sender?,
    @SerializedName("type")
    val type: Type?
) {
    enum class Type {
        @SerializedName("LOCATION")
        LOCATION,
        @SerializedName("JOIN")
        JOIN,
        @SerializedName("LEAVE")
        LEAVE
    }
}