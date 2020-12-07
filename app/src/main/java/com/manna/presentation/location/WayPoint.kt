package com.manna.presentation.location

import com.naver.maps.geometry.LatLng

data class WayPoint(
    val point: LatLng,
    val mode: String,
    val titles: List<String> = emptyList()
) {

    fun getPoint(): String = "${point.latitude},${point.longitude}"
}