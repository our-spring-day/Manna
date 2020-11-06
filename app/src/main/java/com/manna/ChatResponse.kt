package com.manna


import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("sender")
    val sender: Sender?,
    @SerializedName("message")
    val message: Message?,
    @SerializedName("type")
    val type: Type?
) {
    enum class Type {
        @SerializedName("CHAT")
        CHAT,
        @SerializedName("JOIN")
        JOIN,
        @SerializedName("LEAVE")
        LEAVE
    }
}