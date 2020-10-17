package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class ItineraryItem(
    @SerializedName("details")
    val details: List<Detail>?,
    @SerializedName("iconType")
    val iconType: String?,
    @SerializedName("instruction")
    val instruction: Instruction?,
    @SerializedName("isRealTimeTransit")
    val isRealTimeTransit: Boolean?,
    @SerializedName("maneuverPoint")
    val maneuverPoint: ManeuverPoint?,
    @SerializedName("realTimeTransitDelay")
    val realTimeTransitDelay: Int?,
    @SerializedName("sideOfStreet")
    val sideOfStreet: String?,
    @SerializedName("travelDistance")
    val travelDistance: Double?,
    @SerializedName("travelDuration")
    val travelDuration: Int?,
    @SerializedName("travelMode")
    val travelMode: String?,
    @SerializedName("childItineraryItems")
    val childItineraryItems: List<ChildItineraryItem>?,
    @SerializedName("transitLine")
    val transitLine: TransitLine?,
    @SerializedName("transitTerminus")
    val transitTerminus: String?
)