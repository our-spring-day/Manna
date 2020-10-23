package com.manna.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.databinding.library.baseAdapters.BR
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.JsonObject
import com.manna.Logger
import com.manna.R
import com.manna.SocketResponse
import com.manna.UserHolder
import com.manna.common.BaseActivity
import com.manna.common.BaseRecyclerViewAdapter
import com.manna.common.BaseRecyclerViewHolder
import com.manna.databinding.ActivityWebsocketBinding
import com.manna.databinding.ItemRouteBinding
import com.manna.ext.ViewUtil
import com.manna.network.model.meet.MeetResponseItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_websocket.*

class WebSocketTestActivity : BaseActivity<ActivityWebsocketBinding>(R.layout.activity_websocket),
    OnMapReadyCallback {
    //    private val socketConnection: WebSocketConnection = WebSocketConnection()
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var location: Location? = null

    private val locationRequest by lazy {
        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList: List<Location> = locationResult.locations
            if (locationList.isNotEmpty()) {
                location = locationList[locationList.size - 1]

                location?.let {
                    Logger.d("${it.latitude}, ${it.longitude}")

                    val message = JsonObject().apply {
                        addProperty("latitude", it.latitude)
                        addProperty("longitude", it.longitude)
                    }

//                    if (socketConnection.isConnected) {
//                        socketConnection.sendMessage(message.toString())
//                    }
                }
            }
        }
    }

    private val routeAdapter by lazy {
        object :
            BaseRecyclerViewAdapter<String, ItemRouteBinding, BaseRecyclerViewHolder<ItemRouteBinding>>(
                R.layout.item_route,
                variableId = BR.item
            ) {

        }
    }

    private var naverMap: NaverMap? = null
    private val markerMap: HashMap<String, Marker> = hashMapOf()

    @SuppressLint("MissingPermission")
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val meetItem = intent.getParcelableExtra<MeetResponseItem>(MEET_ITEM)
        start()

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

        btn_menu.setOnClickListener {

        }

        BottomSheetBehavior.from(bottom_sheet)
            .addBottomSheetCallback(createBottomSheetCallback(bottom_sheet_state))

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.routeText.adapter = routeAdapter
    }


    override fun onStop() {
        super.onStop()
//        if (fusedLocationClient != null) {
//
//            fusedLocationClient?.removeLocationUpdates(locationCallback)
//        }
    }


    private fun createBottomSheetCallback(text: TextView): BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
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


    private fun start() {
        val wsuri =
            "ws://ec2-54-180-125-3.ap-northeast-2.compute.amazonaws.com:40008/ws?token=${UserHolder.userResponse?.deviceId}" //"ws://ec2-13-124-151-24.ap-northeast-2.compute.amazonaws.com:9999/manna"
//        try {
//            socketConnection.connect(wsuri, object : IWebSocketConnectionHandler {
//
//                override fun onMessage(payload: ByteArray?, isBinary: Boolean) {
//                    Logger.d("$payload $isBinary")
//                }
//
//                override fun onConnect(response: ConnectionResponse?) {
//                    Logger.d("$response")
//                }
//
//                override fun onPing() {
//                    Logger.d("")
//                }
//
//                override fun onPing(payload: ByteArray?) {
//                    Logger.d("$payload")
//                }
//
//                override fun onPong() {
//                    Logger.d("")
//                }
//
//                override fun onPong(payload: ByteArray?) {
//                    Logger.d("$payload")
//                }
//
//                override fun setConnection(connection: WebSocketConnection?) {
//                    Logger.d("$connection")
//                }
//
//                @SuppressLint("SetTextI18n")
//                override fun onOpen() {
//                    Logger.d("Status: Connected to $wsuri")
////                    outputView.text = outputView.text.toString() + "\nconnected to " + wsuri
//                }
//
//                override fun onMessage(payload: String?) {
//                    Logger.d("Got echo: $payload")
//
//                    val socketResponse = Gson().fromJson(payload, SocketResponse::class.java)
//                    Logger.d("socketResponse: $socketResponse")
//
//                    when (socketResponse.type) {
//                        SocketResponse.Type.LOCATION -> {
//                            handleLocation(socketResponse)
//                        }
//                        SocketResponse.Type.JOIN -> {
//                            routeAdapter.add("${socketResponse.sender?.username}님이 들어왔습니다.")
//                        }
//                        SocketResponse.Type.LEAVE -> {
//                            routeAdapter.add("${socketResponse.sender?.username}님이 나갔습니다.")
//                        }
//                    }
//                }
//
//                override fun onClose(code: Int, reason: String?) {
//                    Logger.d("Connection lost.")
//                }
//            })
//        } catch (e: WebSocketException) {
//            Logger.d(e.toString())
//        }
    }

    private fun handleLocation(socketResponse: SocketResponse) {
        socketResponse.sender?.username?.let { fromUserName ->
            val latLng = socketResponse.latLng

            Logger.d("locate: ${latLng?.latitude} ${latLng?.longitude}")
            if (latLng?.latitude != null && latLng.longitude != null) {
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        latLng.latitude,
                        latLng.longitude
                    )
                )
                naverMap?.moveCamera(cameraUpdate)

                val markerView = LayoutInflater.from(this@WebSocketTestActivity)
                    .inflate(R.layout.view_marker, this@WebSocketTestActivity.root_view, false)
                markerView.findViewById<TextView>(R.id.name).text =
                    fromUserName.subSequence(1, fromUserName.length)

                val marker =
                    markerMap[fromUserName] ?: Marker().also { markerMap[fromUserName] = it }
                marker.icon = OverlayImage.fromView(markerView)
                marker.position = LatLng(latLng.latitude, latLng.longitude)
                marker.map = naverMap

            }
        }
    }


    companion object {
        private const val MEET_ITEM = "meet_item"
        private const val UPDATE_INTERVAL_MS = 5000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 5000L

        fun getIntent(context: Context, meetItem: MeetResponseItem) =
            Intent(context, WebSocketTestActivity::class.java).apply {
                putExtra(MEET_ITEM, meetItem)
            }
    }
}