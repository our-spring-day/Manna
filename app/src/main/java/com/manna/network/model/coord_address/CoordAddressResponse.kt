package com.manna.network.model.coord_address


import com.google.gson.annotations.SerializedName

data class CoordAddressResponse(
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("meta")
    val meta: Meta
)