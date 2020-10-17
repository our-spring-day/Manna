package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class ManeuverPoint(
    @SerializedName("type")
    val type: String?,
    @SerializedName("coordinates")
    val coordinates: List<Double>?
)