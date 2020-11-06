package com.manna.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.manna.*
import com.manna.R
import com.manna.ext.ViewUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_meet_detail.*
import java.net.URI
import java.util.*
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

    private val markerMap: HashMap<String?, Marker> = hashMapOf()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val locationRequest by lazy {
        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }
    var layoutId = R.layout.view_round_marker
    lateinit var markerView: View
    private var myLatLng = LatLng(0.0, 0.0)
    private val lastTimeStamp: HashMap<String?, Long> = hashMapOf()


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
                if (MannaApp.locationSocket?.connected() == true) {
                    Logger.d("$message")
                    MannaApp.locationSocket?.emit("location", message)
                }
            }
        }
    }

    private val onLocationReceiver = Emitter.Listener { args ->
        runOnUiThread {
            val message = args.getOrNull(0)

            val socketResponse = Gson().fromJson(message.toString(), LocationResponse::class.java)
            Logger.d("socketResponse: $socketResponse")

            handleLocation(socketResponse)
        }
    }

    private val onLocationConnectReceiver = Emitter.Listener { args ->
        runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }

    lateinit var sheetCallback: BottomSheetCallback

    fun resetBottomSheet(offset: Float) = sheetCallback.onSlide(bottom_sheet, offset)

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

        tab_bottom.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {

                }
            }

        })
        tab_bottom.setupWithViewPager(view_pager)


        val chatFragment = ChatFragment()
        view_pager.run {
            adapter = ViewPagerAdapter(supportFragmentManager).apply {
                addFragment(chatFragment)
                addFragment(RankingFragment())
                addFragment(RankingFragment())
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 3
        }

        btn_location.setOnClickListener {
            moveLocation(myLatLng, 13.0)
        }

        val latitudeList = arrayListOf<Double>()
        val longitudeList = arrayListOf<Double>()
        btn_mountain.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btn_location.visibility = View.VISIBLE
                moveLocation(myLatLng, 13.0)
            } else {
                btn_location.visibility = View.GONE
                markerMap.forEach {
                    latitudeList.add(it.value.position.latitude)
                    longitudeList.add(it.value.position.longitude)
                }
                val cameraUpdate = CameraUpdate.fitBounds(
                    LatLngBounds(
                        LatLng(
                            Collections.min(latitudeList),
                            Collections.min(longitudeList)
                        ), LatLng(Collections.max(latitudeList), Collections.max(longitudeList))
                    ), 20
                )
                naverMap.moveCamera(cameraUpdate)
            }
        }

//                markerMap[user.deviceToken]?.let {
//                    viewModel.findRoute(
//                        user = user,
//                        startPoint = WayPoint(it.position, ""),
//                        endPoint = WayPoint(LatLng(37.475370, 126.980438), "")
//                    )
//                    moveLocation(it)
//                }

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.run {
            drawWayPoints.observe(this@MeetDetailActivity, {
                drawLine(naverMap, it.map { it.point })
            })
            remainValue.observe(
                this@MeetDetailActivity, { (user: User, remainValue) ->


                    val remainDistance = remainValue.first
                    val remainTime = remainValue.second
                    user.remainDistance = remainDistance
                    user.remainTime = remainTime
                })
        }

        BottomSheetBehavior.from(bottom_sheet)
            .also {
                sheetCallback = object : BottomSheetCallback() {
                    override fun onStateChanged(view: View, newState: Int) {

                    }

                    override fun onSlide(view: View, slideOffset: Float) {
                        chatFragment.inputViewTransY(getChatInputY(view).toInt())
                    }
                }
                it.addBottomSheetCallback(sheetCallback)
                Handler().postDelayed({
                    sheetCallback.onSlide(bottom_sheet, 0f)
                }, 50)
            }



        bottom_sheet.maxHeight =
            getBottomSheetFullHeight()
    }

    private fun getChatInputY(rootView: View) =
        rootView.height - rootView.y + getBottomSheetTopMargin()

    private fun getBottomSheetTopMargin() = (ViewUtil.getStatusBarHeight(this) +
            ViewUtil.convertDpToPixel(this, 95f)).toInt()

    private fun getBottomSheetFullHeight() =
        (getWindowHeight() - getBottomSheetTopMargin())

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
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
            isZoomControlEnabled = false
            logoGravity = Gravity.END
            setLogoMargin(0, 0, 0, 0)
        }
        naverMap.setContentPadding(250, 250, 250, 400)

        connect()

        val meetPlaceMarker = Marker().apply {
            position = LatLng(37.475370, 126.980438)
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_arrival_place)
        }

        naverMap.addOnLocationChangeListener { location ->
            myLatLng = LatLng(location.latitude, location.longitude)
        }

        naverMap.setOnMapLongClickListener { point, coord ->
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

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    private fun connect() {
        if (MannaApp.locationSocket?.connected() == true) return

        val options = IO.Options()
        options.query =
            "mannaID=96f35135-390f-496c-af00-cdb3a4104550&deviceToken=${UserHolder.userResponse?.deviceId}"

        val manager = Manager(URI("https://manna.duckdns.org:19999"), options)
        MannaApp.locationSocket =
            manager.socket("/location")?.apply {
                on(LOCATION_CONNECT, onLocationConnectReceiver)
                on(LOCATION_MESSAGE, onLocationReceiver)

                on(Socket.EVENT_CONNECT) {
                    Logger.d("EVENT_CONNECT ${it.map { it.toString() }}")
                }

                on(Socket.EVENT_DISCONNECT) {
                    Logger.d("EVENT_DISCONNECT ${it.map { it.toString() }}")
                }

                on(Socket.EVENT_MESSAGE) {
                    Logger.d("EVENT_MESSAGE ${it.map { it.toString() }}")
                }

                connect()
            }

    }

    private var marker = Marker()

    private fun handleLocation(locationResponse: LocationResponse) {
        locationResponse.sender?.username?.let { fromUserName ->
            val latLng = locationResponse.latLng
            Logger.d("locate: ${latLng?.latitude} ${latLng?.longitude}")

            if (latLng?.latitude != null && latLng.longitude != null) {
                val deviceToken = locationResponse.sender.deviceToken

                markerView = LayoutInflater.from(this)
                    .inflate(layoutId, this.root_view, false)
                if (layoutId == R.layout.view_marker) {
                    markerView.findViewById<TextView>(R.id.name).text = fromUserName
                } else {
                    if (deviceToken != null) {
                        setImage(markerView.findViewById(R.id.iv_image), deviceToken)
                    }
                }

                if (lastTimeStamp.containsKey(deviceToken)) {
                    if (System.currentTimeMillis() - lastTimeStamp[deviceToken]!! > 60000) {
                        marker.alpha = 0.5f
                    }
                } else {
                    marker.alpha = 1f
                }
                lastTimeStamp[deviceToken] = System.currentTimeMillis()

                marker =
                    markerMap[deviceToken] ?: Marker().also { markerMap[deviceToken] = it }
                marker.icon = OverlayImage.fromView(markerView)
                marker.position = LatLng(latLng.latitude, latLng.longitude)
                marker.map = naverMap
            }
        }
    }

    private fun setImage(imageView: ImageView, deviceToken: String) {
        kotlin.runCatching {
            val imageResId = when (deviceToken) {
                "aed64e8da3a07df4" -> R.drawable.test_2
                "f606564d8371e455" -> R.drawable.image_3
                "8F630481-548D-4B8A-B501-FFD90ADFDBA4" -> R.drawable.image_2
                "0954A791-B5BE-4B56-8F25-07554A4D6684" -> R.drawable.image_4
                "C65CDF73-8C04-4F76-A26A-AE3400FEC14B" -> R.drawable.image_6
                "69751764-A224-4923-9844-C61646743D10" -> R.drawable.image_1
                "2872483D-9E7B-46D1-A2B8-44832FE3F1AD" -> R.drawable.image_5
                "8D44FAA1-2F87-4702-9DAC-B8B15D949880" -> R.drawable.image_7
                else -> R.drawable.test_1
            }

            Glide.with(this).load(imageResId).into(imageView)
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

    private fun moveLocation(latLng: LatLng, zoom: Double) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, zoom)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun getLinearEquation(
        point1: LatLng,
        point2: LatLng,
        point: Double,
        value: String
    ): LatLng {
        val a =
            (point2.latitude - point1.latitude) / (point2.longitude - point1.longitude)
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
        val a =
            (point2.latitude - point1.latitude) / (point2.longitude - point1.longitude)
        val b = -(a * point1.longitude) + point1.latitude
        val c =
            (markerPoint.latitude - centerPoint.latitude) / (markerPoint.longitude - centerPoint.longitude)
        val d = -(c * centerPoint.longitude) + centerPoint.latitude
        return LatLng((a * (d - b) / (a - c)) + b, ((d - b) / (a - c)))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val UPDATE_INTERVAL_MS = 2000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 2000L

        private const val LOCATION_CONNECT = "locationConnect"
        private const val LOCATION_MESSAGE = "location"

        fun getIntent(context: Context) =
            Intent(context, MeetDetailActivity::class.java)
    }
}