package com.manna.view

import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.manna.R
import com.manna.ext.ViewUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_meet_detail.*
import org.java_websocket.client.DefaultSSLWebSocketClientFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
import javax.net.ssl.SSLContext
import kotlin.concurrent.timer
import kotlin.math.sqrt

class MeetDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var webSocketClient: WebSocketClient
    private var myLatLng = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_detail)
        connect()
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        ViewUtil.setStatusBarTransparent(this)

        top_panel.fitsSystemWindows = true

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Face
        naverMap.isIndoorEnabled = true

        naverMap.addOnLocationChangeListener { location ->
            myLatLng = LatLng(location.latitude, location.longitude)
        }

        val uiSettings = naverMap.uiSettings
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isLocationButtonEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isScaleBarEnabled = false
        uiSettings.logoGravity = Gravity.END
        uiSettings.setLogoMargin(0, 80, 60, 0)

        val meetPlaceMarker = Marker()
        meetPlaceMarker.apply {
            position = LatLng(37.557527, 126.9222782)
            map = naverMap
            icon = MarkerIcons.BLACK
            iconTintColor = Color.RED
        }

        iv_test_1.setOnClickListener {
            moveLocation(LatLng(37.566065, 126.9804903))
        }

        iv_test_2.setOnClickListener {
            moveLocation(LatLng(37.534542, 126.9924073))
        }

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val projection = naverMap.projection

        val newMarker = Marker()

        naverMap.addOnCameraChangeListener { reason, animated ->
            val topBottomLatLng: LatLng
            val startEndLatLng: LatLng
            val markerPoint = projection.toScreenLocation(meetPlaceMarker.position)
            val center =
                projection.fromScreenLocation(PointF(size.x / 2.toFloat(), size.y / 2.toFloat()))
            val topStart = projection.fromScreenLocation(PointF(0f, 0f))
            val topEnd = projection.fromScreenLocation(PointF(size.x.toFloat(), 0f))
            val bottomStart = projection.fromScreenLocation(PointF(0f, size.y.toFloat()))
            val bottomEnd =
                projection.fromScreenLocation(PointF(size.x.toFloat(), size.y.toFloat()))

            if (markerPoint.x >= 0 && markerPoint.x <= size.x && markerPoint.y >= 0 && markerPoint.y <= size.y) {
                newMarker.map = null
            } else {
                newMarker.map = null
                if (markerPoint.x > size.x / 2) {
                    startEndLatLng = getLatLng(topEnd, bottomEnd, center, meetPlaceMarker.position)
                    newMarker.angle = 0f
                } else {
                    startEndLatLng =
                        getLatLng(topStart, bottomStart, center, meetPlaceMarker.position)
                    newMarker.angle = 0f
                }
                if (markerPoint.y > size.y / 2) {
                    topBottomLatLng =
                        getLatLng(bottomStart, bottomEnd, center, meetPlaceMarker.position)
                    newMarker.angle = 0f
                } else {
                    topBottomLatLng = getLatLng(topStart, topEnd, center, meetPlaceMarker.position)
                    newMarker.angle = 180f
                }

                val startEnd = sqrt(
                    (startEndLatLng.latitude - center.latitude) * (startEndLatLng.latitude - center.latitude) +
                            (startEndLatLng.longitude - center.longitude) * (startEndLatLng.longitude - center.longitude)
                )
                val topBottom = sqrt(
                    (topBottomLatLng.latitude - center.latitude) * (topBottomLatLng.latitude - center.latitude) +
                            (topBottomLatLng.longitude - center.longitude) * (topBottomLatLng.longitude - center.longitude)
                )

                if (startEnd < topBottom) {
                    newMarker.position = startEndLatLng
                    newMarker.map = naverMap
                    newMarker.icon =
                        OverlayImage.fromResource(R.drawable.ic_baseline_account_circle_24)
                } else if (startEnd > topBottom) {
                    newMarker.position = topBottomLatLng
                    newMarker.map = naverMap
                    newMarker.icon =
                        OverlayImage.fromResource(R.drawable.ic_baseline_account_circle_24)
                }
            }
        }
    }

    private fun connect() {
        val url = "ws://ec2-54-180-125-3.ap-northeast-2.compute.amazonaws.com:40008/ws?token=2"
        var uri: URI? = null
        uri = try {
            URI(url)
        } catch (e: URISyntaxException) {
            Log.e(TAG, e.message!!)
            return
        }
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {
                Log.e(TAG, "Connect")
                setMyLocation()
            }

            override fun onMessage(message: String) {
                Log.e(TAG, "Message: $message")
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                Log.e(TAG, "Disconnect")
            }

            override fun onError(ex: java.lang.Exception) {
                Log.e(TAG, "Error: " + ex.message)
            }
        }
        if (url.indexOf("wss") == 0) {
            try {
                val sslContext: SSLContext = SSLContext.getDefault()
                webSocketClient.setWebSocketFactory(DefaultSSLWebSocketClientFactory(sslContext))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        webSocketClient.connect()
    }

    private fun setMyLocation() {
        val timer = timer(period = 10000) {
            webSocketClient.send("{\"latitude\":${myLatLng.latitude},\"longitude\":${myLatLng.longitude}}")
        }
    }

    private fun moveLocation(latLng: LatLng) {
        val marker = Marker()
        marker.map = null
        marker.position = latLng
        marker.map = naverMap
        marker.icon =
            OverlayImage.fromView(TextView(this).apply {
                setBackgroundColor(resources.getColor(R.color.darkGray))
                setTextColor(Color.WHITE)
                text = "연재"

                setPadding(30, 30, 30, 30)
            })
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun getLocation(latLng: LatLng) {
        val marker = Marker()
        marker.map = null
        marker.position = latLng
        marker.map = naverMap
        marker.icon =
            OverlayImage.fromView(TextView(this).apply {
                setBackgroundColor(resources.getColor(R.color.darkGray))
                setTextColor(Color.WHITE)
                text = "연재"
                setPadding(30, 30, 30, 30)
            })
    }

    private fun getLatLng(
        point1: LatLng,
        point2: LatLng,
        centerPoint: LatLng,
        markerPoint: LatLng
    ): LatLng {
        val a = (point2.longitude - point1.longitude) / (point2.latitude - point1.latitude)
        val b = -(a * point1.latitude) + point1.longitude
        val c =
            (markerPoint.longitude - centerPoint.longitude) / (markerPoint.latitude - centerPoint.latitude)
        val d = -(c * centerPoint.latitude) + centerPoint.longitude
        return LatLng(((d - b) / (a - c)), (a * (d - b) / (a - c)) + b)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val TAG = "MeetDetailActivity:"
    }
}