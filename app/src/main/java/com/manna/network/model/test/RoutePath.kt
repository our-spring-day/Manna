package com.manna.network.model.test


import com.google.gson.annotations.SerializedName

data class RoutePath(
    @SerializedName("generalizations")
    val generalizations: List<Any>?,
    @SerializedName("line")
    val line: Line?
)