package com.manna.view.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.manna.*
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityMeetDetailBinding
import com.manna.databinding.ViewRoundMarkerBinding
import com.manna.ext.ViewUtil
import com.manna.view.User
import com.manna.view.chat.ChatFragment
import com.manna.view.rank.RankingFragment
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
import kotlin.collections.set

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

    var overlayState = DEFAULT

    var meetPlaceMarker = Marker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        markerHolders.clear()
        fusedLocationProvider = FusedLocationProvider(this, fusedLocationCallback)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        countDown()

        updateBtn()

        initView()

        initViewModel()

        LocationSocketManager.setLocationResponseCallback {
            runOnUiThread {
                if (UserHolder.deviceId != it.sender?.deviceToken) {
                    handleLocation(it)
                }
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

            cvBack.setOnClickListener {
                onBackPressed()
            }

            btnLocation.setOnCheckedChangeListener { buttonView, isChecked ->
                when (overlayState) {
                    DEFAULT -> {
                        if (btnMountain.isChecked) {
                            overlayState = TRACKING
                            naverMap.locationTrackingMode = LocationTrackingMode.Face
                            markerHolders.forEach {
                                if (it.uuid == UserHolder.userResponse?.deviceId) {
                                    it.marker.isVisible = false
                                }
                            }
                        }
                    }
                    ACTIVE -> {
                        overlayState = DEFAULT
                        if (btnMountain.isChecked) {
                            moveLocation(myLatLng, 13.0)
                        } else {
                            moveLocation()
                        }
                    }
                    TRACKING -> {
                        overlayState = DEFAULT
                        naverMap.locationTrackingMode = LocationTrackingMode.Follow
                        markerHolders.forEach {
                            if (it.uuid == UserHolder.userResponse?.deviceId) {
                                it.marker.isVisible = true
                            }
                        }
                    }
                }
                updateBtn()
            }

            btnMountain.setOnCheckedChangeListener { _, isChecked ->
                markerHolders.forEach {
                    if (it.uuid == UserHolder.userResponse?.deviceId) {
                        it.marker.isVisible = true
                    }
                }
                if (isChecked) {
                    moveLocation(myLatLng, 13.0)
                } else {
                    moveLocation()
                }
                overlayState = DEFAULT
                updateBtn()
            }

            cvChatting.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment =
                    supportFragmentManager.findFragmentByTag(ChatFragment::class.java.simpleName)

                if (fragment != null) {
                    transaction.show(fragment).commit()
                } else {
                    transaction
                        .replace(
                            R.id.frag_container,
                            ChatFragment.newInstance(roomId),
                            ChatFragment::class.java.simpleName
                        )
                        .commit()
                }
            }

            cvRanking.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment =
                    supportFragmentManager.findFragmentByTag(RankingFragment::class.java.simpleName)

                if (fragment != null) {
                    transaction.show(fragment).commit()
                } else {
                    transaction
                        .replace(
                            R.id.frag_container,
                            RankingFragment.newInstance(roomId),
                            RankingFragment::class.java.simpleName
                        )
                        .commit()
                }
            }

        }
    }

    private fun updateBtn() {
        var btnDrawable = R.drawable.ic_map_default
        when (overlayState) {
            DEFAULT -> btnDrawable = R.drawable.ic_map_default
            ACTIVE -> btnDrawable = R.drawable.ic_map_active
            TRACKING -> btnDrawable = R.drawable.ic_map_tracking
        }
        binding.btnLocation.setButtonDrawable(btnDrawable)
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
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.ic_location_overlay)

        fusedLocationProvider.enableLocationCallback()

        this.naverMap = naverMap.apply {
            this.locationSource = this@MeetDetailActivity.locationSource
            locationTrackingMode = LocationTrackingMode.NoFollow
            isIndoorEnabled = true

            uiSettings.run {
                isIndoorLevelPickerEnabled = true
                isLocationButtonEnabled = false
                isCompassEnabled = false
                isScaleBarEnabled = false
                isZoomControlEnabled = false
                setLogoMargin(36, 0, 0, 36)
            }

            addOnLocationChangeListener { location ->
                handleLocation(
                    LocationResponse(
                        MyLatLng(location.latitude, location.longitude),
                        Sender(UserHolder.deviceId, UserHolder.userResponse?.username),
                        LocationResponse.Type.LOCATION
                    )
                )
            }
        }

        checkState()

        meetPlaceMarker.apply {
            position = LatLng(37.475370, 126.980438)
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_arrival_place)
        }

        naverMap.minZoom = 5.0
        naverMap.maxZoom = 18.0
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        naverMap.addOnCameraChangeListener { reason, animated ->
//            if (naverMap.cameraPosition.zoom > 13.0) {
//                markerHolders.forEach {
//                    it.marker.width = size.y / naverMap.cameraPosition.zoom.toInt()
//                    it.marker.height = size.y / naverMap.cameraPosition.zoom.toInt()
//                }
//            } else {
//                markerHolders.forEach {
//                    it.marker.width = size.y / 13
//                    it.marker.height = size.y / 13
//                }
//            }

            if (reason == REASON_GESTURE) {
                markerHolders.forEach {
                    if (it.uuid == UserHolder.userResponse?.deviceId) {
                        it.marker.isVisible = true
                    }
                }
                overlayState = ACTIVE
                updateBtn()
                binding.btnLocation.visibility = View.VISIBLE
                naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
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

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frag_container)

        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
            return
        }

        super.onBackPressed()
    }


    private fun checkState() {
        Handler().postDelayed({
            for (key in lastTimeStamp.keys) {
                if (System.currentTimeMillis() - lastTimeStamp[key]!! > 60000) {
                    markerHolders.forEach {
                        if (it.uuid == key) {
                            it.marker.alpha = 0.5f
                        }
                    }
                } else {
                    markerHolders.forEach {
                        if (it.uuid == key) {
                            it.marker.alpha = 1f
                        }
                    }
                }
            }
            checkState()
        }, 1000L)
    }

    private fun handleLocation(locationResponse: LocationResponse) {
        checkUserType(locationResponse)
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
                                    if (deviceToken.orEmpty() == UserHolder.deviceId) {
                                        this.updatePadding(
                                            bottom = ViewUtil.convertDpToPixel(
                                                this@MeetDetailActivity,
                                                10f
                                            ).toInt()
                                        )
                                    }
                                    findViewById<TextView>(R.id.name).text = fromUserName
                                }


                            val marker =
                                ViewRoundMarkerBinding.inflate(
                                    LayoutInflater.from(this),
                                    binding.rootView,
                                    false
                                )
                                    .apply {
                                        if (deviceToken.orEmpty() == UserHolder.deviceId) {
                                            this.root.updatePadding(
                                                bottom = ViewUtil.convertDpToPixel(
                                                    this@MeetDetailActivity,
                                                    10f
                                                ).toInt()
                                            )
                                        }
                                        setImage(
                                            this.ivImage,
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
                                    marker.root
                                )
                            )

                            it.icon = OverlayImage.fromView(markerView)
                            it.width = Marker.SIZE_AUTO
                            it.height = Marker.SIZE_AUTO
                        }

                lastTimeStamp[deviceToken] = System.currentTimeMillis()

                marker.run {
                    position = LatLng(latLng.latitude, latLng.longitude)
                    map = naverMap
                }

            }
        }
    }

    private fun countDown() {
        val startTime = Calendar.getInstance().run {
            set(2020, 10, 17, 23, 0, 0)
            timeInMillis
        }
        val endTime = Calendar.getInstance().run {
            set(2020, 10, 17, 24, 0, 0)
            timeInMillis
        }
        val nowTime = System.currentTimeMillis()

        if (startTime > nowTime) {
            binding.timerLayout.isGone = true
            return
        }

        val timer = object : CountDownTimer(endTime - nowTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val getMin =
                    millisUntilFinished - millisUntilFinished / (60 * 60 * 1000)
                var min = (getMin / (60 * 1000)).toString()
                var second = (getMin % (60 * 1000) / 1000).toString()

                if (min.length == 1) {
                    min = "0$min"
                }

                if (second.length == 1) {
                    second = "0$second"
                }
                if (min == "20" && second == "00") {
                    binding.timerLayout.setBackgroundResource(R.drawable.bg_timer_yellow)
                } else if (min == "10" && second == "00") {
                    binding.timerLayout.setBackgroundResource(R.drawable.bg_timer_red)
                }
                binding.remainMinute.text = min
                binding.remainSeconds.text = second
            }

            override fun onFinish() {
                binding.timerLayout.setBackgroundResource(R.drawable.bg_timer)
                binding.checkIn.isVisible = true
                binding.timerGroup.isGone = true
            }
        }
        timer.start()
    }

    private fun checkUserType(locationResponse: LocationResponse) {
        if (locationResponse.type == LocationResponse.Type.LEAVE) {
            markerHolders.forEach {
                if (it.uuid == locationResponse.sender?.deviceToken) {
                    it.markerView.background =
                        ContextCompat.getDrawable(this, R.drawable.marker_box_red)
                }
            }
        } else if (locationResponse.type == LocationResponse.Type.JOIN) {
            markerHolders.forEach {
                if (it.uuid == locationResponse.sender?.deviceToken) {
                    it.markerView.background =
                        ContextCompat.getDrawable(this, R.drawable.marker_box)
                }
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


            Glide.with(this)
                .applyDefaultRequestOptions(
                    RequestOptions.bitmapTransform(
                        RoundedCorners(
                            ViewUtil.convertDpToPixel(
                                this,
                                24f
                            ).toInt()
                        )
                    )
                )
                .load(imageResId)
                .into(imageView)
        }
    }

    private fun moveLocation(latLng: LatLng, zoom: Double) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, zoom)
        naverMap.moveCamera(cameraUpdate)
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    private fun moveLocation() {
        val latitudeList = mutableListOf<Double>()
        val longitudeList = mutableListOf<Double>()
        markerHolders.forEach {
            latitudeList.add(it.marker.position.latitude)
            longitudeList.add(it.marker.position.longitude)
        }
        latitudeList.add(meetPlaceMarker.position.latitude)
        longitudeList.add(meetPlaceMarker.position.longitude)

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
                250
            )
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val EXTRA_ROOM_ID = "room_id"
        private const val DEFAULT = "default"
        private const val ACTIVE = "active"
        private const val TRACKING = "tracking"

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