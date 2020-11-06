package com.manna


import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("sender")
    val sender: Sender?,
    @SerializedName("message")
    val message: Message?
)