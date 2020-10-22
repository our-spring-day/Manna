package com.manna

import com.google.gson.annotations.SerializedName

data class SocketResponse(
    @SerializedName("createTime")
    val createTime: String?,
    @SerializedName("from")
    val from: SocketUser?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("to")
    val socket: List<SocketUser>?
)