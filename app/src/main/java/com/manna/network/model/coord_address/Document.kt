package com.manna.network.model.coord_address


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("address")
    val address: Address?,
    @SerializedName("road_address")
    val roadAddress: RoadAddress?
)