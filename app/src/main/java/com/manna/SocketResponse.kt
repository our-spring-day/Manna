package com.manna

import com.google.gson.annotations.SerializedName

data class SocketResponse(
    @SerializedName("location")
    val latLng: MyLatLng?,
    @SerializedName("sender")
    val sender: Sender?,
)