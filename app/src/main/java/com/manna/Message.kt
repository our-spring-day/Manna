package com.manna


import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("createTimestamp")
    val createTimestamp: Long?,
    @SerializedName("message")
    val message: String?
)