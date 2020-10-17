package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class Instruction(
    @SerializedName("formattedText")
    val formattedText: Any?,
    @SerializedName("maneuverType")
    val maneuverType: String?,
    @SerializedName("text")
    val text: String?
)