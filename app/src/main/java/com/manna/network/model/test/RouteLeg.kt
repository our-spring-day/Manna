package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class RouteLeg(
    @SerializedName("actualEnd")
    val actualEnd: ActualEnd?,
    @SerializedName("actualStart")
    val actualStart: ActualStart?,
    @SerializedName("alternateVias")
    val alternateVias: List<Any>?,
    @SerializedName("cost")
    val cost: Int?,
    @SerializedName("endTime")
    val endTime: String?,
    @SerializedName("itineraryItems")
    val itineraryItems: List<ItineraryItem>?,
    @SerializedName("routeRegion")
    val routeRegion: String?,
    @SerializedName("routeSubLegs")
    val routeSubLegs: List<RouteSubLeg>?,
    @SerializedName("startTime")
    val startTime: String?,
    @SerializedName("travelDistance")
    val travelDistance: Double?,
    @SerializedName("travelDuration")
    val travelDuration: Int?,
    @SerializedName("travelMode")
    val travelMode: String?
)