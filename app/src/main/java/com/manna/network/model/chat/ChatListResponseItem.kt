package com.manna.network.model.chat


import com.google.gson.annotations.SerializedName

data class ChatListResponseItem(
    @SerializedName("createTimestamp")
    val createTimestamp: Long?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("sender")
    val sender: Sender?
)