package com.manna.network.model.meet


import com.google.gson.annotations.SerializedName

data class MeetResponseItem(
    @SerializedName("manna_name")
    val mannaName: String?,
    @SerializedName("create_timestamp")
    val createTimestamp: Long?,
    @SerializedName("uuid")
    val uuid: String?
)