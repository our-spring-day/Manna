package com.manna.ext

import java.util.*

fun String.parseMsTimestampToDate(): Date {
    val dateParts = this.replace("/Date(", "").replace(")/", "").split("-")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateParts[0].toLong()
    return calendar.time
}