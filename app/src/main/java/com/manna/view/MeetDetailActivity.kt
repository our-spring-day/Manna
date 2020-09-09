package com.manna.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.manna.R
import com.manna.ext.ViewUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_meet_detail.*


class MeetDetailActivity : AppCompatActivity(), OnMapReadyCallback{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_detail)

        ViewUtil.setStatusBarTransparent(this)

        top_panel.fitsSystemWindows = true

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        btn_back.setOnClickListener {
            onBackPressed()
        }

        btn_menu.setOnClickListener {
            drawer.openDrawer(side_panel)
        }

//        BottomSheetBehavior.from(bottom_sheet)
//            .addBottomSheetCallback(createBottomSheetCallback(bottom_sheet_state))
    }

    private fun createBottomSheetCallback(text: TextView): BottomSheetCallback =
        object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                text.text = when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> "STATE DRAGGING"
                    BottomSheetBehavior.STATE_EXPANDED -> "STATE EXPANDED"
                    BottomSheetBehavior.STATE_COLLAPSED -> "STATE COLLAPSED"
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        String.format(
                            "STATE_HALF_EXPANDED\\nhalfExpandedRatio = %.2f",
                            BottomSheetBehavior.from(bottomSheet).halfExpandedRatio
                        )
                    }
                    else -> {
                        text.text.toString()
                    }
                }
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
            }
        }

    override fun onMapReady(naverMap: NaverMap) {
        val marker = Marker()
        val meetPlaceMarker = Marker()
        meetPlaceMarker.position = LatLng(37.5670135, 126.9783740)
        meetPlaceMarker.map = naverMap
        meetPlaceMarker.icon = MarkerIcons.BLACK
        meetPlaceMarker.iconTintColor = Color.RED

        iv_test_1.setOnClickListener {
            marker.map = null
            marker.position = LatLng(37.5670135, 126.9983740)
            marker.map = naverMap
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
            naverMap.moveCamera(cameraUpdate)
        }

        iv_test_2.setOnClickListener {
            marker.map = null
            marker.position = LatLng(37.5970135, 126.9783740)
            marker.map = naverMap
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
            naverMap.moveCamera(cameraUpdate)
        }
    }
}
