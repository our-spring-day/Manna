package com.manna.network.model.chat


import com.google.gson.annotations.SerializedName

data class Sender(
    @SerializedName("deviceToken")
    val deviceToken: String?,
    @SerializedName("username")
    val username: String?
)