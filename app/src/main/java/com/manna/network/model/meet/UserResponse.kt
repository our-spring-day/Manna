package com.manna.network.model.meet


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("device_id")
    val deviceId: String?,
    @SerializedName("username")
    val username: String?
)