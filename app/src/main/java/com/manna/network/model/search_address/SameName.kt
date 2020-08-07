package com.manna.network.model.search_address


import com.google.gson.annotations.SerializedName

data class SameName(
    @SerializedName("keyword")
    val keyword: String,
    @SerializedName("region")
    val region: List<Any>,
    @SerializedName("selected_region")
    val selectedRegion: String
)