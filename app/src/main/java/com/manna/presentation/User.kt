package com.manna.presentation

data class User(
    val name: String,
    val deviceToken: String,
    val latitude: Double,
    val longitude: Double,
    var remainDistance: Double? = null,
    var remainTime: Int? = null
)