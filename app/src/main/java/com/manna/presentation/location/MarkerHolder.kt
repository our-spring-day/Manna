package com.manna.presentation.location

import android.view.View
import com.naver.maps.map.overlay.Marker

class MarkerHolder(
    val uuid: String,
    val marker: Marker,
    val markerView: View,
    val imageMarkerView: View,
    var marketState: MarkerState = MarkerState.NORMAL,
)