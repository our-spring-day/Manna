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
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_websocket.*
import okhttp3.OkHttpClient
import java.net.URI
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SocketIOTestActivity : BaseActivity<ActivityWebsocketBinding>(R.layout.activity_websocket),
    OnMapReadyCallback {

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

                    val message = JsonObject().apply {
                        addProperty("latitude", it.latitude)
                        addProperty("longitude", it.longitude)
                    }

                    if (locationSocket.connected()) {
                        Logger.d("$message")
                        locationSocket.emit("location", message)
                    }
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

    private lateinit var locationSocket: Socket
    private lateinit var chatSocket: Socket
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

    private val onLocationReceiver = Emitter.Listener { args ->
        runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }

    private val onLocationConnectReceiver = Emitter.Listener { args ->
        runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }

    private val onChatConnectReceiver = Emitter.Listener { args ->
        runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }


    private val onChatReceiver = Emitter.Listener { args ->
        runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }


    override fun onDestroy() {
        locationSocket.disconnect()
        locationSocket.off(LOCATION_CONNECT, onLocationReceiver)

        chatSocket.disconnect()
        chatSocket.off(CHAT_CONNECT, onChatReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.edtSubmit.setOnClickListener {
            chatSocket.emit(CHAT_MESSAGE, binding.edtChat.text.toString())
        }

        val myHostnameVerifier = HostnameVerifier { _, _ ->
            return@HostnameVerifier true
        }

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)

        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier(myHostnameVerifier)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .build()


        val options = IO.Options()
        options.query = "mannaID=96f35135-390f-496c-af00-cdb3a4104550&deviceToken=f606564d8371e455"
        options.callFactory = okHttpClient
        options.webSocketFactory = okHttpClient
//        options.transports = arrayOf(Polling.NAME)

        val manager = Manager(URI("https://manna.duckdns.org:19999"), options)
        locationSocket =
            manager.socket("/location")
        locationSocket.on(LOCATION_CONNECT, onLocationConnectReceiver)
        locationSocket.on(LOCATION_MESSAGE, onLocationReceiver)

        locationSocket.on(Socket.EVENT_CONNECT) {
            Logger.d("EVENT_CONNECT ${it.map { it.toString() }}")
        }

        locationSocket.on(Socket.EVENT_DISCONNECT) {
            Logger.d("EVENT_DISCONNECT ${it.map { it.toString() }}")
        }

        locationSocket.on(Socket.EVENT_MESSAGE) {
            Logger.d("EVENT_MESSAGE ${it.map { it.toString() }}")
        }

        locationSocket.connect()

        val chatManager = Manager(URI("https://manna.duckdns.org:19999"), options)
        chatSocket =
            chatManager.socket("/chat")
        chatSocket.on(CHAT_CONNECT, onChatConnectReceiver)
        chatSocket.on(CHAT_MESSAGE, onChatReceiver)

        chatSocket.on(Socket.EVENT_CONNECT) {
            Logger.d("EVENT_CONNECT ${it.map { it.toString() }}")
        }

        chatSocket.on(Socket.EVENT_DISCONNECT) {
            Logger.d("EVENT_DISCONNECT ${it.map { it.toString() }}")
        }

        chatSocket.on(Socket.EVENT_MESSAGE) {
            Logger.d("EVENT_MESSAGE ${it.map { it.toString() }}")
        }

        chatSocket.connect()



        val meetItem = intent.getParcelableExtra<MeetResponseItem>(MEET_ITEM)

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

                val markerView = LayoutInflater.from(this@SocketIOTestActivity)
                    .inflate(R.layout.view_marker, this@SocketIOTestActivity.root_view, false)
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
        private const val UPDATE_INTERVAL_MS = 1000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 1000L

        private const val LOCATION_CONNECT = "locationConnect"
        private const val CHAT_CONNECT = "chatConnect"

        private const val LOCATION_MESSAGE = "location"
        private const val CHAT_MESSAGE = "chat"


        fun getIntent(context: Context, meetItem: MeetResponseItem) =
            Intent(context, SocketIOTestActivity::class.java).apply {
                putExtra(MEET_ITEM, meetItem)
            }
    }
}