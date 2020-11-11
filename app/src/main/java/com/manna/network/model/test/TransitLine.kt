package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class TransitLine(
    @SerializedName("abbreviatedName")
    val abbreviatedName: String?,
    @SerializedName("headSigns")
    val headSigns: List<HeadSign>?,
    @SerializedName("verboseName")
    val verboseName: String?
)