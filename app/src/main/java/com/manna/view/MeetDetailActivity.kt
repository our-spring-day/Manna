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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.manna.CircleImageView
import com.manna.Logger
import com.manna.R
import com.manna.SocketResponse
import com.manna.ext.ViewUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_meet_detail.*
import kotlinx.android.synthetic.main.activity_meet_detail.btn_back
import kotlinx.android.synthetic.main.activity_meet_detail.top_panel
import org.java_websocket.client.DefaultSSLWebSocketClientFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
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
    private var myLatLng = LatLng(0.0, 0.0)
    private val markerMap: HashMap<String?, Marker> = hashMapOf()
    private val meetDetailAdapter = MeetDetailAdapter()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val locationRequest by lazy {
        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }
    var layoutId = R.layout.view_round_marker

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
            onBackPressed()
        }

        btn_info.setOnClickListener {
            meetDetailAdapter.setItemViewType()
            val markerView = LayoutInflater.from(this)
                .inflate(layoutId, this.root_view, false)
            if(layoutId == R.layout.view_round_marker){
                layoutId = R.layout.view_marker
                markerMap.forEach{
                    it.value.icon = OverlayImage.fromView(markerView)
                    markerView.findViewById<TextView>(R.id.name).text =
                        it.key?.length?.let { it1 -> it.key?.subSequence(1, it1) }
                }
            } else {
                layoutId = R.layout.view_round_marker
                markerMap.forEach{
                    it.value.icon = OverlayImage.fromView(markerView)
                    Glide.with(this).load(R.drawable.test_1).into(markerView.findViewById<CircleImageView>(R.id.iv_image))
                }
            }
        }

        rv_user.layoutManager = GridLayoutManager(this, 4)
        rv_user.adapter = meetDetailAdapter
        meetDetailAdapter.setOnClickListener(object : MeetDetailAdapter.OnClickListener {
            override fun onClick(user: User) {
                markerMap[user.deviceToken]?.let { moveLocation(it) }
            }
        })

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            isLocationButtonEnabled = true
            isCompassEnabled = false
            isScaleBarEnabled = false
            logoGravity = Gravity.END
            setLogoMargin(0, 80, 60, 0)
        }

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
                } else if (startEnd > topBottom) {
                    newMarker.position = topBottomLatLng
                    newMarker.map = naverMap
                }
            }
        }
    }

    private fun connect() {
        val url =
            "ws://ec2-54-180-125-3.ap-northeast-2.compute.amazonaws.com:40008/ws?token=aed64e8da3a07df4"
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
                    Toast.makeText(
                        applicationContext,
                        socketResponse.latLng.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

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

    private fun handleLocation(socketResponse: SocketResponse) {
        socketResponse.sender?.username?.let { fromUserName ->
            val latLng = socketResponse.latLng
            Logger.d("locate: ${latLng?.latitude} ${latLng?.longitude}")
            if (latLng?.latitude != null && latLng.longitude != null) {
                val markerView = LayoutInflater.from(this)
                    .inflate(layoutId, this.root_view, false)
                if(layoutId == R.layout.view_marker){
                    markerView.findViewById<TextView>(R.id.name).text =
                        fromUserName.subSequence(1, fromUserName.length)
                } else {
                    Glide.with(this).load(R.drawable.test_1).into(markerView.findViewById<CircleImageView>(R.id.iv_image))
                }

                val name = socketResponse.sender.username
                val latitude = socketResponse.latLng.latitude
                val longitude = socketResponse.latLng.longitude
                val deviceToken = socketResponse.sender.deviceToken

                val marker =
                    markerMap[fromUserName] ?: Marker().also { markerMap[fromUserName] = it }
                marker.icon = OverlayImage.fromView(markerView)
                marker.position = LatLng(latLng.latitude, latLng.longitude)
                marker.map = naverMap

                if (!markerMap.containsKey(deviceToken)) {
                    meetDetailAdapter.addData(User(name, deviceToken, latitude, longitude))
                } else {
                    meetDetailAdapter.refreshItem(User(name, deviceToken, latitude, longitude))
                }
                markerMap[deviceToken] = marker

            }
        }
    }

//    private fun setMyLocation() {
//        val timer = timer(period = 10000) {
//            webSocketClient.send("{\"latitude\":${myLatLng.latitude},\"longitude\":${myLatLng.longitude}}")
//        }
//    }

    private fun moveLocation(marker: Marker) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 16.0)
        naverMap.moveCamera(cameraUpdate)
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
        private const val TAG = "MeetDetailActivity:"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val UPDATE_INTERVAL_MS = 5000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 5000L

        fun getIntent(context: Context) =
            Intent(context, MeetDetailActivity::class.java)
    }
}


//class MeetDetailActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private val viewModel: MeetDetailViewModel by viewModels()
//
//    @SuppressLint("CheckResult")
//    @UiThread
//    override fun onMapReady(naverMap: NaverMap) {
//
//        val endPoint = WayPoint(LatLng(37.492642, 127.026208), "End")
//
//        ApiModule.provideBingApi()
//            .getRoute(
//                startLatLng = "37.482087,126.976742",
//                endLatLng = endPoint.getPoint()
//            )
//            .subscribeOn(Schedulers.io())
//            .map { root ->
//                val items =
//                    root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems
//
//                val points = mutableListOf<WayPoint>()
//
//                items?.forEach {
//                    val mode = it.details?.first()?.mode
//
//                    it.maneuverPoint?.coordinates?.let { point ->
//                        points.add(WayPoint(LatLng(point[0], point[1]), mode.orEmpty()))
//                    }
//                }
//
//                points.add(endPoint)
//                points
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ list ->
//
//                val sources = list.mapIndexedNotNull { index, wayPoint ->
//                    when (wayPoint.mode) {
//                        "Transit" -> {
//                            ApiModule.provideBingApi()
//                                .getRouteDriving(
//                                    "${wayPoint.point.latitude},${wayPoint.point.longitude}",
//                                    "${list.get(index + 1).point.latitude},${list.get(index + 1).point.longitude}"
//                                )
//                                .subscribeOn(Schedulers.io())
//                                .map { root ->
//                                    val items =
//                                        root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems
//
//                                    val points = mutableListOf<WayPoint>()
////                                    points.add(wayPoint)
//
//                                    items?.forEach {
//                                        val mode = it.details?.first()?.mode
//
//                                        it.maneuverPoint?.coordinates?.let { point ->
//                                            points.add(
//                                                WayPoint(
//                                                    LatLng(point[0], point[1]),
//                                                    mode.orEmpty()
//                                                )
//                                            )
//                                        }
//                                    }
//
//                                    points
//                                }
//                                .observeOn(AndroidSchedulers.mainThread())
//                        }
//                        "Walking" -> {
//                            ApiModule.provideBingApi()
//                                .getRouteWalking(
//                                    "${wayPoint.point.latitude},${wayPoint.point.longitude}",
//                                    "${list.get(index + 1).point.latitude},${list.get(index + 1).point.longitude}"
//                                )
//                                .subscribeOn(Schedulers.io())
//                                .map { root ->
//                                    val items =
//                                        root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems
//
//                                    val points = mutableListOf<WayPoint>()
////                                    points.add(wayPoint)
//
//                                    items?.forEach {
//                                        val mode = it.details?.first()?.mode
//
//                                        it.maneuverPoint?.coordinates?.let { point ->
//                                            points.add(
//                                                WayPoint(
//                                                    LatLng(point[0], point[1]),
//                                                    mode.orEmpty()
//                                                )
//                                            )
//                                        }
//                                    }
//
//                                    points
//                                }
//                                .observeOn(AndroidSchedulers.mainThread())
//                        }
//                        else -> {
//                            null
//                        }
//                    }
//                }
//
//                Observable.zip(sources) {
//                    it
//                }
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({ array ->
//                        val list = array.flatMap {
//                            it as List<WayPoint>
//                        }.toMutableList()
//                        list.add(endPoint)
//
//                        list.forEach {
//                            Logger.d("$it")
//                        }
//
//                        drawLine(naverMap, list.map { it.point })
//
//                        val cameraUpdate = CameraUpdate.scrollTo(list.first().point)
//                        naverMap.moveCamera(cameraUpdate)
//
//                    }, {
//                        Logger.d("$it")
//                    })
//
//            }, {
//                Logger.d("$it")
//            })
//    }
//
//    private fun drawLine(naverMap: NaverMap, points: List<LatLng>) {
//        val multipartPath = MultipartPathOverlay()
//
//        multipartPath.coordParts = listOf(
//            points
////            listOf(
////                LatLng(37.5744287, 126.982625),
////                LatLng(37.57152, 126.97714),
////                LatLng(37.56607, 126.98268)
////            ),
////            listOf(
////                LatLng(37.56607, 126.98268),
////                LatLng(37.55845, 126.98207),
////                LatLng(37.55855, 126.97822)
////            ),
////            listOf(
////                LatLng(37.56607, 126.98268),
////                LatLng(37.56345, 126.97607),
////                LatLng(37.56755, 126.96722)
////            ),
////            listOf(
////                LatLng(37.56607, 126.98268),
////                LatLng(37.56445, 126.99707),
////                LatLng(37.55855, 126.99822)
////            )
//        )
//
//        multipartPath.colorParts = listOf(
//            MultipartPathOverlay.ColorPart(
//                Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY
//            )
////            ,
////            MultipartPathOverlay.ColorPart(
////                Color.GREEN, Color.WHITE, Color.DKGRAY, Color.LTGRAY
////            ),
////            MultipartPathOverlay.ColorPart(
////                Color.BLUE, Color.WHITE, Color.DKGRAY, Color.LTGRAY
////            ),
////            MultipartPathOverlay.ColorPart(
////                Color.BLACK, Color.WHITE, Color.DKGRAY, Color.LTGRAY
////            )
//        )
//
//        multipartPath.map = naverMap
//
//    }
//
//}