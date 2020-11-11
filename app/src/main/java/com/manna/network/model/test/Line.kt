package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class Line(
    @SerializedName("type")
    val type: String?,
    @SerializedName("coordinates")
    val coordinates: List<List<Double>>?
)