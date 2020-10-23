package com.manna.view

data class Response(
    val sender: Sender,
    val location: Location?,
    val type: String
)