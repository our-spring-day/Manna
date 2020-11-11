package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class RouteSubLeg(
    @SerializedName("endWaypoint")
    val endWaypoint: EndWaypoint?,
    @SerializedName("startWaypoint")
    val startWaypoint: StartWaypoint?,
    @SerializedName("travelDistance")
    val travelDistance: Double?,
    @SerializedName("travelDuration")
    val travelDuration: Int?
)