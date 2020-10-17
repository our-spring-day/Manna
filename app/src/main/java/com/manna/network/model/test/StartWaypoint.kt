package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class StartWaypoint(
    @SerializedName("type")
    val type: String?,
    @SerializedName("coordinates")
    val coordinates: List<Double>?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("isVia")
    val isVia: Boolean?,
    @SerializedName("locationIdentifier")
    val locationIdentifier: String?,
    @SerializedName("routePathIndex")
    val routePathIndex: Int?
)