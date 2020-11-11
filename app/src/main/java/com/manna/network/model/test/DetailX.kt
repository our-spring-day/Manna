package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class DetailX(
    @SerializedName("maneuverType")
    val maneuverType: String?,
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("names")
    val names: List<String>?,
    @SerializedName("roadType")
    val roadType: String?
)