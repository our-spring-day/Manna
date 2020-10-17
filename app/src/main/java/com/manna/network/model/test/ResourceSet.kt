package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class ResourceSet(
    @SerializedName("estimatedTotal")
    val estimatedTotal: Int?,
    @SerializedName("resources")
    val resources: List<Resource>?
)