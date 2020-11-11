package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class ChildItineraryItem(
    @SerializedName("details")
    val details: List<DetailX>?,
    @SerializedName("iconType")
    val iconType: String?,
    @SerializedName("instruction")
    val instruction: InstructionX?,
    @SerializedName("isRealTimeTransit")
    val isRealTimeTransit: Boolean?,
    @SerializedName("maneuverPoint")
    val maneuverPoint: ManeuverPointX?,
    @SerializedName("realTimeTransitDelay")
    val realTimeTransitDelay: Int?,
    @SerializedName("sideOfStreet")
    val sideOfStreet: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("transitStopId")
    val transitStopId: Int?,
    @SerializedName("travelDistance")
    val travelDistance: Int?,
    @SerializedName("travelDuration")
    val travelDuration: Int?,
    @SerializedName("travelMode")
    val travelMode: String?
)