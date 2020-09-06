package com.manna.network.model.search_address


import com.google.gson.annotations.SerializedName

data class SearchAddressResponse(
    @SerializedName("documents")
    val searchAddresses: List<SearchAddress>,
    @SerializedName("meta")
    val meta: Meta
)