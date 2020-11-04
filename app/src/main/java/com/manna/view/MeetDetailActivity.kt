package com.manna.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.manna.*
import com.manna.R
import com.manna.ext.ViewUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_meet_detail.*
import org.java_websocket.client.DefaultSSLWebSocketClientFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.net.ssl.SSLContext
import kotlin.math.sqrt

enum class WayMode(val type: String) {
    WALKING("Walking"),
    TRANSIT("Transit")
}

data class WayPoint(
    val point: LatLng,
    val mode: String,
    val titles: List<String> = emptyList()
) {

    fun getPoint(): String = "${point.latitude},${point.longitude}"
}

@AndroidEntryPoint
class MeetDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var webSocketClient: WebSocketClient

    private val markerMap: HashMap<String?, Marker> = hashMapOf()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val locationRequest by lazy {
        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }
    var layoutId = R.layout.view_round_marker
    lateinit var markerView: View

    private val viewModel by viewModels<MeetDetailViewModel>()

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList: List<Location> = locationResult.locations

            locationList.getOrNull(locationList.size - 1)?.let {
                val message = JsonObject().apply {
                    addProperty("latitude", it.latitude)
                    addProperty("longitude", it.longitude)
                }
                Logger.d("$message")
                try {
                    webSocketClient.send(message.toString())
                } catch (e: Exception) {

                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_detail)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
        ViewUtil.setStatusBarTransparent(this)
        top_panel.fitsSystemWindows = true

        btn_back.setOnClickListener {
            //onBackPressed()
            //meetDetailAdapter.changeItem()
        }

        tab_bottom.addTab(tab_bottom.newTab().setIcon(R.drawable.ic_test_01))
        tab_bottom.addTab(tab_bottom.newTab().setIcon(R.drawable.ic_test_02))
        tab_bottom.addTab(tab_bottom.newTab().setIcon(R.drawable.ic_test_03))

        val badge = tab_bottom.getTabAt(0)?.orCreateBadge
        badge?.number = 2

        tab_bottom.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){

                }
            }

        })

        //tab_bottom.setupWithViewPager(view_pager)

        view_pager.run {
            adapter = ViewPagerAdapter(supportFragmentManager).apply {
                addFragment(RankingFragment())
                addFragment(RankingFragment())
                addFragment(RankingFragment())
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 3
        }

        btn_info.setOnClickListener {
            //meetDetailAdapter.setItemViewType()
            if (layoutId == R.layout.view_round_marker) {
                layoutId = R.layout.view_marker
                markerView = LayoutInflater.from(this)
                    .inflate(layoutId, this.root_view, false)
                for (i in markerMap.keys) {
                    markerView.findViewById<TextView>(R.id.name).text = setName(i.toString())
                    markerMap[i]?.icon = OverlayImage.fromView(markerView)
                }
            } else {
                layoutId = R.layout.view_round_marker
                markerView = LayoutInflater.from(this)
                    .inflate(layoutId, this.root_view, false)
                for (i in markerMap.keys) {
                    setImage(markerView.findViewById(R.id.iv_image), i.toString())
                    markerMap[i]?.icon = OverlayImage.fromView(markerView)
                }
            }
        }

//        meetDetailAdapter.setOnClickListener(object : MeetDetailAdapter.OnClickListener {
//            override fun onClick(user: User) {
//                markerMap[user.deviceToken]?.let {
//                    viewModel.findRoute(
//                        user = user,
//                        startPoint = WayPoint(it.position, ""),
//                        endPoint = WayPoint(LatLng(37.475370, 126.980438), "")
//                    )
//                    moveLocation(it)
//                }
//            }
//        })

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.run {
            drawWayPoints.observe(this@MeetDetailActivity, {
                drawLine(naverMap, it.map { it.point })
            })
            remainValue.observe(this@MeetDetailActivity, { (user: User, remainValue) ->
                Logger.d("$user")
                Logger.d("$remainValue")

                val remainDistance = remainValue.first
                val remainTime = remainValue.second
                user.remainDistance = remainDistance
                user.remainTime = remainTime
//                meetDetailAdapter.refreshItem(user)
            })
        }
    }

    private fun drawLine(naverMap: NaverMap, points: List<LatLng>) {
        val multipartPath = MultipartPathOverlay()

        multipartPath.coordParts = listOf(points)

        multipartPath.colorParts = listOf(
            MultipartPathOverlay.ColorPart(
                Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY
            )
        )

        multipartPath.map = naverMap
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(naverMap: NaverMap) {

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.isIndoorEnabled = true
        naverMap.uiSettings.run {
            isIndoorLevelPickerEnabled = true
            isLocationButtonEnabled = false
            isCompassEnabled = false
            isScaleBarEnabled = false
            logoGravity = Gravity.END
            setLogoMargin(0, 80, 60, 0)
        }
        btn_location.map = naverMap

        connect()

        val meetPlaceMarker = Marker().apply {
            position = LatLng(37.557527, 126.9222782)
            map = naverMap
            icon = MarkerIcons.BLACK
            iconTintColor = Color.RED
        }

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val projection = naverMap.projection

        val newMarker = Marker()
        newMarker.icon =
            OverlayImage.fromResource(R.drawable.ic_baseline_account_circle_24)

        naverMap.addOnCameraChangeListener { reason, animated ->
            val topBottomLatLng: LatLng
            val startEndLatLng: LatLng
            val markerPoint = projection.toScreenLocation(meetPlaceMarker.position)
            val center =
                projection.fromScreenLocation(
                    PointF(
                        size.x / 2.toFloat(),
                        size.y / 2.toFloat()
                    )
                )
            val topStart = projection.fromScreenLocation(PointF(0f, 0f))
            val topEnd = projection.fromScreenLocation(PointF(size.x.toFloat(), 0f))
            val bottomStart = projection.fromScreenLocation(PointF(0f, size.y.toFloat()))
            val bottomEnd =
                projection.fromScreenLocation(PointF(size.x.toFloat(), size.y.toFloat()))

            if (markerPoint.x >= 0 && markerPoint.x <= size.x && markerPoint.y >= 0 && markerPoint.y <= size.y) {
                newMarker.map = null
            } else {
                newMarker.map = null
                if (topStart.latitude == topEnd.latitude || topStart.longitude == bottomStart.longitude) {
                    startEndLatLng = if (markerPoint.x > size.x / 2) {
                        getLinearEquation(center, meetPlaceMarker.position, topEnd.longitude, "x")
                    } else {
                        getLinearEquation(center, meetPlaceMarker.position, topStart.longitude, "x")
                    }
                    topBottomLatLng = if (markerPoint.y > size.y / 2) {
                        getLinearEquation(
                            center,
                            meetPlaceMarker.position,
                            bottomStart.latitude,
                            "y"
                        )
                    } else {
                        getLinearEquation(center, meetPlaceMarker.position, topStart.latitude, "y")
                    }
                } else {
                    startEndLatLng = if (markerPoint.x > size.x / 2) {
                        getLatLng(topEnd, bottomEnd, center, meetPlaceMarker.position)
                    } else {
                        getLatLng(topStart, bottomStart, center, meetPlaceMarker.position)
                    }
                    topBottomLatLng = if (markerPoint.y > size.y / 2) {
                        getLatLng(bottomStart, bottomEnd, center, meetPlaceMarker.position)
                    } else {
                        getLatLng(topStart, topEnd, center, meetPlaceMarker.position)
                    }
                }
                val startEnd = sqrt(
                    (startEndLatLng.longitude - center.longitude) * (startEndLatLng.longitude - center.longitude) +
                            (startEndLatLng.latitude - center.latitude) * (startEndLatLng.latitude - center.latitude)
                )
                val topBottom = sqrt(
                    (topBottomLatLng.longitude - center.longitude) * (topBottomLatLng.longitude - center.longitude) +
                            (topBottomLatLng.latitude - center.latitude) * (topBottomLatLng.latitude - center.latitude)
                )
                if (startEnd < topBottom) {
                    newMarker.position = startEndLatLng
                    newMarker.map = naverMap
                } else if (startEnd > topBottom) {
                    newMarker.position = topBottomLatLng
                    newMarker.map = naverMap
                }
            }
        }
    }

    private fun connect() {
        val url =
            "ws://ec2-54-180-125-3.ap-northeast-2.compute.amazonaws.com:40008/ws?token=${UserHolder.userResponse?.deviceId}"
        val uri = try {
            URI(url)
        } catch (e: URISyntaxException) {
            Log.e(TAG, e.message)
            return
        }
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {
                Log.e(TAG, "Connect")
//                setMyLocation()
            }

            override fun onMessage(message: String) {
                Log.e(TAG, "Message: $message")
                runOnUiThread {

                    val socketResponse = Gson().fromJson(message, SocketResponse::class.java)
                    Logger.d("socketResponse: $socketResponse")

                    if (socketResponse.sender?.username == "이연재") {
                        Toast.makeText(
                            applicationContext,
                            socketResponse.latLng.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    when (socketResponse.type) {
                        SocketResponse.Type.LOCATION -> {
                            handleLocation(socketResponse)
                        }
                        SocketResponse.Type.JOIN -> {
//                            routeAdapter.add("${socketResponse.sender?.username}님이 들어왔습니다.")
                        }
                        SocketResponse.Type.LEAVE -> {
//                            routeAdapter.add("${socketResponse.sender?.username}님이 나갔습니다.")
                        }
                    }
                }
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

    private var marker = Marker()
    private fun handleLocation(socketResponse: SocketResponse) {
        socketResponse.sender?.username?.let { fromUserName ->
            val latLng = socketResponse.latLng
            Logger.d("locate: ${latLng?.latitude} ${latLng?.longitude}")
            if (latLng?.latitude != null && latLng.longitude != null) {
                val deviceToken = socketResponse.sender.deviceToken

                markerView = LayoutInflater.from(this)
                    .inflate(layoutId, this.root_view, false)
                if (layoutId == R.layout.view_marker) {
                    markerView.findViewById<TextView>(R.id.name).text =
                        fromUserName.subSequence(1, fromUserName.length)
                } else {
                    if (deviceToken != null) {
                        setImage(markerView.findViewById(R.id.iv_image), deviceToken)
                    }
                }

                if (!markerMap.containsKey(deviceToken)) {
//                    meetDetailAdapter.addData(
//                        User(
//                            fromUserName,
//                            deviceToken,
//                            latLng.latitude,
//                            latLng.longitude
//                        )
//                    )
                } else {
//                    meetDetailAdapter.refreshItem(
//                        User(
//                            fromUserName,
//                            deviceToken,
//                            latLng.latitude,
//                            latLng.longitude
//                        )
//                    )
                }
                marker = markerMap[deviceToken] ?: Marker().also { markerMap[deviceToken] = it }
                marker.icon = OverlayImage.fromView(markerView)
                marker.position = LatLng(latLng.latitude, latLng.longitude)
                marker.map = naverMap
            }
        }
    }

    private fun setImage(imageView: CircleImageView, deviceToken: String) {
        when (deviceToken) {
            "aed64e8da3a07df4" -> Glide.with(this).load(R.drawable.test_2).into(imageView)
            "f606564d8371e455" -> Glide.with(this).load(R.drawable.image_3).into(imageView)
            "8F630481-548D-4B8A-B501-FFD90ADFDBA4" -> Glide.with(this).load(R.drawable.image_2)
                .into(
                    imageView
                )
            "0954A791-B5BE-4B56-8F25-07554A4D6684" -> Glide.with(this).load(R.drawable.image_4)
                .into(
                    imageView
                )
            "C65CDF73-8C04-4F76-A26A-AE3400FEC14B" -> Glide.with(this).load(R.drawable.image_6)
                .into(
                    imageView
                )
            "69751764-A224-4923-9844-C61646743D10" -> Glide.with(this).load(R.drawable.image_1)
                .into(
                    imageView
                )
            "2872483D-9E7B-46D1-A2B8-44832FE3F1AD" -> Glide.with(this).load(R.drawable.image_5)
                .into(
                    imageView
                )
            "8D44FAA1-2F87-4702-9DAC-B8B15D949880" -> Glide.with(this).load(R.drawable.image_7)
                .into(
                    imageView
                )
            else -> Glide.with(this).load(R.drawable.test_1).into(imageView)
        }
    }

    private fun setName(deviceToken: String): String {
        return when (deviceToken) {
            "aed64e8da3a07df4" -> "연재"
            "f606564d8371e455" -> "우석"
            "8F630481-548D-4B8A-B501-FFD90ADFDBA4" -> "상원"
            "0954A791-B5BE-4B56-8F25-07554A4D6684" -> "재인"
            "C65CDF73-8C04-4F76-A26A-AE3400FEC14B" -> "종찬"
            "69751764-A224-4923-9844-C61646743D10" -> "용권"
            "2872483D-9E7B-46D1-A2B8-44832FE3F1AD" -> "규리"
            "8D44FAA1-2F87-4702-9DAC-B8B15D949880" -> "효근"
            else -> ""
        }
    }

    private fun moveLocation(marker: Marker) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun getLinearEquation(
        point1: LatLng,
        point2: LatLng,
        point: Double,
        value: String
    ): LatLng {
        val a = (point2.latitude - point1.latitude) / (point2.longitude - point1.longitude)
        val b = -(a * point1.longitude) + point1.latitude
        return if (value == "x") {
            LatLng((a * point) + b, point)
        } else {
            LatLng(point, (point - b) / a)
        }
    }

    private fun getLatLng(
        point1: LatLng,
        point2: LatLng,
        centerPoint: LatLng,
        markerPoint: LatLng
    ): LatLng {
        val a = (point2.latitude - point1.latitude) / (point2.longitude - point1.longitude)
        val b = -(a * point1.longitude) + point1.latitude
        val c =
            (markerPoint.latitude - centerPoint.latitude) / (markerPoint.longitude - centerPoint.longitude)
        val d = -(c * centerPoint.longitude) + centerPoint.latitude
        return LatLng((a * (d - b) / (a - c)) + b, ((d - b) / (a - c)))
    }

    companion object {
        private const val TAG = "MeetDetailActivity:"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val UPDATE_INTERVAL_MS = 5000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 5000L

        fun getIntent(context: Context) =
            Intent(context, MeetDetailActivity::class.java)
    }
}