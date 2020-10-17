package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class Resource(
    @SerializedName("__type")
    val type: String?,
    @SerializedName("bbox")
    val bbox: List<Double>?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("distanceUnit")
    val distanceUnit: String?,
    @SerializedName("durationUnit")
    val durationUnit: String?,
    @SerializedName("routeLegs")
    val routeLegs: List<RouteLeg>?,
    @SerializedName("routePath")
    val routePath: RoutePath?,
    @SerializedName("trafficCongestion")
    val trafficCongestion: String?,
    @SerializedName("trafficDataUsed")
    val trafficDataUsed: String?,
    @SerializedName("travelDistance")
    val travelDistance: Double?,
    @SerializedName("travelDuration")
    val travelDuration: Int?,
    @SerializedName("travelDurationTraffic")
    val travelDurationTraffic: Int?,
    @SerializedName("travelMode")
    val travelMode: String?
)