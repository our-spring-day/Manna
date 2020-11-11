package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class RootResponse(
    @SerializedName("authenticationResultCode")
    val authenticationResultCode: String?,
    @SerializedName("brandLogoUri")
    val brandLogoUri: String?,
    @SerializedName("copyright")
    val copyright: String?,
    @SerializedName("resourceSets")
    val resourceSets: List<ResourceSet>?,
    @SerializedName("statusCode")
    val statusCode: Int?,
    @SerializedName("statusDescription")
    val statusDescription: String?,
    @SerializedName("traceId")
    val traceId: String?
)