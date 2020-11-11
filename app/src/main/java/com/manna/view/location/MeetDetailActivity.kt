package com.manna.view.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.manna.*
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityMeetDetailBinding
import com.manna.ext.ViewUtil
import com.manna.view.User
import com.manna.view.chat.ChatFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MeetDetailActivity :
    BaseActivity<ActivityMeetDetailBinding>(R.layout.activity_meet_detail),
    OnMapReadyCallback {

    private lateinit var fusedLocationProvider: FusedLocationProvider
    private val fusedLocationCallback: (Location) -> Unit = { location ->
        val message = JsonObject().apply {
            addProperty("latitude", location.latitude)
            addProperty("longitude", location.longitude)
        }
        myLatLng = LatLng(location.latitude, location.longitude)
        LocationSocketManager.sendMessage("location", message.toString())
    }

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val markerHolders: MutableSet<MarkerHolder> = mutableSetOf()

    private var myLatLng = LatLng(0.0, 0.0)
    private val lastTimeStamp: HashMap<String?, Long> = hashMapOf()

    private val roomId: String
        get() = intent?.getStringExtra(EXTRA_ROOM_ID).orEmpty()

    private val multipartPath = MultipartPathOverlay()

    private val viewModel by viewModels<MeetDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        markerHolders.clear()
        fusedLocationProvider = FusedLocationProvider(this, fusedLocationCallback)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        initView()

        initViewModel()

        LocationSocketManager.setLocationResponseCallback {
            runOnUiThread {
                handleLocation(it)
            }
        }
        LocationSocketManager.connect(roomId)
    }

    private fun initView() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
        ViewUtil.setStatusBarTransparent(this)
        binding.run {
            topPanel.fitsSystemWindows = true

            btnBack.setOnClickListener {
                onBackPressed()
            }

            btnLocation.setOnClickListener {
                if (btnMountain.isChecked) {
                    moveLocation(myLatLng, 13.0)
                } else {
                    moveLocation()
                }
            }

            btnMountain.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    moveLocation(myLatLng, 13.0)
                } else {
                    moveLocation()
                }
            }

            btnChatting.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .add(
                        R.id.frag_container,
                        ChatFragment.newInstance(roomId),
                        ChatFragment::class.java.simpleName
                    )
                    .addToBackStack(null)
                    .commit()
            }

            btnChart.setOnClickListener {

            }

        }
    }

    private fun initViewModel() {
        viewModel.run {
            drawWayPoints.observe(this@MeetDetailActivity, androidx.lifecycle.Observer {
                drawLine(naverMap, it.map { it.point })
            })

            remainValue.observe(
                this@MeetDetailActivity,
                androidx.lifecycle.Observer { (user: User, remainValue) ->
                    val remainDistance = remainValue.first
                    val remainTime = remainValue.second
                    user.remainDistance = remainDistance
                    user.remainTime = remainTime

                    val userList = viewModel.userList.value.orEmpty().run {
                        val index = indexOfFirst { it.deviceToken == user.deviceToken }
                        val list = toMutableList()
                        if (index != -1) {
                            list[index] = user
                        } else {
                            list.add(user)
                        }

                        list
                    }

                    viewModel.submitUserList(userList)
                })
            bottomUserItemClickEvent.observe(this@MeetDetailActivity, EventObserver { clickUser ->
                markerHolders.find { it.uuid == clickUser.deviceToken }?.let {
                    viewModel.findRoute(
                        user = clickUser,
                        startPoint = WayPoint(it.marker.position, ""),
                        endPoint = WayPoint(LatLng(37.475370, 126.980438), "")
                    )
                    moveLocation(it.marker.position, 13.0)
                }
            })
        }
    }

    private fun drawLine(naverMap: NaverMap, points: List<LatLng>) {
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

        fusedLocationProvider.enableLocationCallback()

        this.naverMap = naverMap.apply {
            locationSource = locationSource
            locationTrackingMode = LocationTrackingMode.NoFollow
            isIndoorEnabled = true
            uiSettings.run {
                isIndoorLevelPickerEnabled = true
                isLocationButtonEnabled = false
                isCompassEnabled = false
                isScaleBarEnabled = false
                isZoomControlEnabled = false
            }
        }

        val meetPlaceMarker = Marker().apply {
            position = LatLng(37.475370, 126.980438)
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_arrival_place)
        }

        var cameraZoom = naverMap.cameraPosition.zoom
        naverMap.addOnCameraChangeListener { reason, animated ->
            if (cameraZoom > naverMap.cameraPosition.zoom) {

            } else {

            }
            cameraZoom = naverMap.cameraPosition.zoom
            if (reason == REASON_GESTURE) {
                naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
                binding.btnLocation.visibility = View.VISIBLE
            }
        }

        naverMap.setOnMapLongClickListener { point, coord ->
            markerHolders.forEach {
                it.marker.icon = when (it.marketState) {
                    MarkerState.NORMAL -> {
                        it.marketState = MarkerState.IMAGE
                        OverlayImage.fromView(it.imageMarkerView)
                    }
                    MarkerState.IMAGE -> {
                        it.marketState = MarkerState.NORMAL
                        OverlayImage.fromView(it.markerView)
                    }
                }
                it.marker.map = naverMap
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        fusedLocationProvider.enableLocationCallback()
    }

    override fun onStop() {
        fusedLocationProvider.disableLocationCallback()
        super.onStop()
    }


    private fun checkState(marker: Marker, deviceToken: String) {
        for (key in lastTimeStamp.keys) {
            lastTimeStamp[key]
            markerHolders

        }
        if (lastTimeStamp.containsKey(deviceToken)) {
            if (System.currentTimeMillis() - lastTimeStamp[deviceToken]!! > 60000) {
                marker.alpha = 0.5f
            }
        } else {
            marker.alpha = 1f
        }
        lastTimeStamp[deviceToken] = System.currentTimeMillis()
    }

    private fun handleLocation(locationResponse: LocationResponse) {
        locationResponse.sender?.username?.let { fromUserName ->
            val latLng = locationResponse.latLng
            Logger.d("locate: ${latLng?.latitude} ${latLng?.longitude}")

            if (latLng?.latitude != null && latLng.longitude != null) {
                val deviceToken = locationResponse.sender.deviceToken
                if (deviceToken.isNullOrEmpty()) return@let

                val marker =
                    markerHolders.find { it.uuid == deviceToken }?.marker
                        ?: Marker().also {

                            it.setOnClickListener {
                                true
                            }
                            val markerView = LayoutInflater.from(this)
                                .inflate(R.layout.view_marker, binding.rootView, false)
                                .apply {
                                    findViewById<TextView>(R.id.name).text = fromUserName
                                }

                            val imageMarkerView = LayoutInflater.from(this)
                                .inflate(R.layout.view_round_marker, binding.rootView, false)
                                .apply {
                                    setImage(
                                        findViewById<CircleImageView>(R.id.iv_image),
                                        deviceToken.orEmpty()
                                    )
                                }

                            viewModel.addUser(
                                User(fromUserName, deviceToken, latLng.latitude, latLng.longitude)
                            )

                            markerHolders.add(
                                MarkerHolder(
                                    deviceToken,
                                    it,
                                    markerView,
                                    imageMarkerView
                                )
                            )

                            it.icon = OverlayImage.fromView(markerView)
                        }

                marker.run {
                    checkState(this, deviceToken)
                    position = LatLng(latLng.latitude, latLng.longitude)
                    map = naverMap
                }

            }
        }
    }

    private fun setImage(imageView: CircleImageView, deviceToken: String) {
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
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, zoom)
            .finishCallback {
                binding.btnLocation.visibility = View.GONE
            }

        naverMap.moveCamera(cameraUpdate)
    }

    private fun moveLocation() {
        val latitudeList = mutableListOf<Double>()
        val longitudeList = mutableListOf<Double>()
        markerHolders.forEach {
            latitudeList.add(it.marker.position.latitude)
            longitudeList.add(it.marker.position.longitude)
        }

        val cameraUpdate =
            CameraUpdate.fitBounds(
                LatLngBounds(
                    LatLng(
                        Collections.min(latitudeList),
                        Collections.min(longitudeList)
                    ),
                    LatLng(
                        Collections.max(latitudeList),
                        Collections.max(longitudeList)
                    )
                ),
                20
            ).finishCallback {
                binding.btnLocation.visibility = View.GONE
            }
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val EXTRA_ROOM_ID = "room_id"

        fun getIntent(context: Context, roomId: String) =
            Intent(
                context,
                MeetDetailActivity::class.java
            ).apply {
                putExtra(EXTRA_ROOM_ID, roomId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
    }
}