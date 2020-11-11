package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("endPathIndices")
    val endPathIndices: List<Int>?,
    @SerializedName("maneuverType")
    val maneuverType: String?,
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("names")
    val names: List<Any>?,
    @SerializedName("roadType")
    val roadType: String?,
    @SerializedName("startPathIndices")
    val startPathIndices: List<Int>?
)