package com.manna.view.chat


import com.google.gson.annotations.SerializedName
import com.manna.Message
import com.manna.Sender

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