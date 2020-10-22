package com.manna
import com.google.gson.annotations.SerializedName

data class SocketUser(
    @SerializedName("deviceToken")
    val deviceToken: String?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("username")
    val username: String?
)