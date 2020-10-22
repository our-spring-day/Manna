package com.manna.view.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.databinding.library.baseAdapters.BR
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manna.Logger
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseRecyclerViewAdapter
import com.manna.common.BaseRecyclerViewHolder
import com.manna.databinding.ActivityRouteBinding
import com.manna.databinding.ItemRouteBinding
import com.manna.di.ApiModule
import com.manna.ext.ViewUtil
import com.manna.view.WayPoint
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_meet_detail.*

class RouteActivity : BaseActivity<ActivityRouteBinding>(R.layout.activity_route),
    OnMapReadyCallback {

    companion object {
        const val FIND_POINT = "find_point"
        private const val UPDATE_INTERVAL_MS = 5000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 5000L
        fun getIntent(context: Context, findPoint: LatLng) =
            Intent(context, RouteActivity::class.java).apply {
                putExtra(FIND_POINT, findPoint)
            }
    }

    var currentLocation: Location? = null
    var currentPosition: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

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
                    currentPosition = LatLng(it.latitude, it.longitude)
                }
                currentLocation = location
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

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        mFusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    @SuppressLint("CheckResult")
    private fun findRoute(startPoint: WayPoint, endPoint: WayPoint) {
        ApiModule.provideBingApi()
            .getRoute(
                startLatLng = startPoint.getPoint(),
                endLatLng = endPoint.getPoint()
            )
            .subscribeOn(Schedulers.io())
            .map { root ->
                val items =
                    root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems

                val paths =
                    root.resourceSets?.first()?.resources?.first()?.routePath?.line?.coordinates

                val points = mutableListOf<WayPoint>()

                Logger.d("$paths")

                paths?.forEach { path ->

                    if (path.size > 1) {
                        points.add(WayPoint(LatLng(path[0], path[1]), ""))
                    }
                }


                val titles = mutableListOf<String>()

                items?.forEach {
                    val mode = it.details?.first()?.mode
                    val title = it.instruction?.text

                    if (!title.isNullOrEmpty()) {
                        titles.add(title)
                    }
                    val childTitles = it.childItineraryItems?.mapNotNull {
                        it.instruction?.text
                    }
                    if (childTitles != null) {
                        titles.addAll(childTitles)
                    }

                    if (mode == "Transit") {
                        Logger.d("$it")
                    }
                }

                points to titles
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (list, titles) ->

                val cameraUpdate = CameraUpdate.scrollTo(list.first().point)
                naverMap?.moveCamera(cameraUpdate)
                drawLine(naverMap!!, list.map { it.point })
                routeAdapter.replaceAll(titles)

                return@subscribe
                val sources = list.mapIndexedNotNull { index, wayPoint ->
                    when (wayPoint.mode) {
                        "Transit" -> {
                            ApiModule.provideBingApi()
                                .getRouteDriving(
                                    wayPoint.getPoint(),
                                    "${list.get(index + 1).point.latitude},${list.get(index + 1).point.longitude}"
                                )
                                .subscribeOn(Schedulers.io())
                                .map { root ->
                                    val items =
                                        root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems

                                    val points = mutableListOf<WayPoint>()
                                    points.add(wayPoint)

                                    items?.forEach {
                                        val mode = it.details?.first()?.mode
                                        val title =
                                            it.instruction?.text.orEmpty()

                                        it.maneuverPoint?.coordinates?.let { point ->
                                            points.add(
                                                WayPoint(
                                                    LatLng(point[0], point[1]),
                                                    mode.orEmpty(),
//                                                    title
                                                )
                                            )
                                        }
                                    }

                                    points
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                        }
                        "Walking" -> {
                            ApiModule.provideBingApi()
                                .getRouteWalking(
                                    "${wayPoint.point.latitude},${wayPoint.point.longitude}",
                                    "${list.get(index + 1).point.latitude},${list.get(index + 1).point.longitude}"
                                )
                                .subscribeOn(Schedulers.io())
                                .map { root ->
                                    val items =
                                        root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems

                                    val points = mutableListOf<WayPoint>()
                                    points.add(wayPoint)

                                    items?.forEach {
                                        val mode = it.details?.first()?.mode
                                        val title =
                                            it.instruction?.text.orEmpty()

                                        it.maneuverPoint?.coordinates?.let { point ->
                                            points.add(
                                                WayPoint(
                                                    LatLng(point[0], point[1]),
                                                    mode.orEmpty(),
                                                )
                                            )
                                        }
                                    }

                                    points
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                        }
                        else -> {
                            null
                        }
                    }
                }

                Observable.zip(sources) {
                    it
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ array ->
                        array.forEach {
                            Logger.d("${(it as List<WayPoint>).size}")
                        }

                        val list = array.flatMap {
                            it as List<WayPoint>
                        }.toMutableList()
                        list.add(endPoint)

                        list.forEach {
                            Logger.d("$it")
                        }

                        drawLine(naverMap!!, list.map { it.point })

                        routeAdapter.replaceAll(list.flatMap { it.titles })
                        val markerPoints = list.filter { it.titles.isNotEmpty() }
                        markerPoints.forEach {
                            val marker = Marker()
                            marker.position = it.point
                            marker.map = naverMap
                        }

                        val cameraUpdate = CameraUpdate.scrollTo(list.first().point)
                        naverMap?.moveCamera(cameraUpdate)

                    }, {
                        Logger.d("$it")
                    })

            }, {
                Logger.d("$it")
            })
    }

    private fun drawLine(naverMap: NaverMap, points: List<LatLng>) {
        val multipartPath = MultipartPathOverlay()

        multipartPath.coordParts = listOf(
            points
//            listOf(
//                LatLng(37.5744287, 126.982625),
//                LatLng(37.57152, 126.97714),
//                LatLng(37.56607, 126.98268)
//            ),
//            listOf(
//                LatLng(37.56607, 126.98268),
//                LatLng(37.55845, 126.98207),
//                LatLng(37.55855, 126.97822)
//            ),
//            listOf(
//                LatLng(37.56607, 126.98268),
//                LatLng(37.56345, 126.97607),
//                LatLng(37.56755, 126.96722)
//            ),
//            listOf(
//                LatLng(37.56607, 126.98268),
//                LatLng(37.56445, 126.99707),
//                LatLng(37.55855, 126.99822)
//            )
        )

        multipartPath.colorParts = listOf(
            MultipartPathOverlay.ColorPart(
                Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY
            )
//            ,
//            MultipartPathOverlay.ColorPart(
//                Color.GREEN, Color.WHITE, Color.DKGRAY, Color.LTGRAY
//            ),
//            MultipartPathOverlay.ColorPart(
//                Color.BLUE, Color.WHITE, Color.DKGRAY, Color.LTGRAY
//            ),
//            MultipartPathOverlay.ColorPart(
//                Color.BLACK, Color.WHITE, Color.DKGRAY, Color.LTGRAY
//            )
        )

        multipartPath.map = naverMap

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            drawer.openDrawer(side_panel)
        }

        BottomSheetBehavior.from(bottom_sheet)
            .addBottomSheetCallback(createBottomSheetCallback(bottom_sheet_state))


        Handler().postDelayed({
            val findPoint = intent.getParcelableExtra<LatLng>(FIND_POINT)
            findPoint?.let {

                Logger.d("currentLocation $currentLocation")
                currentLocation?.let { currentLocation ->
                    findRoute(
                        WayPoint(
                            LatLng(currentLocation.latitude, currentLocation.longitude),
                            "Start"
                        ),
                        WayPoint(it, "End")
                    )
                }

            }

        }, 1000)


        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        binding.routeText.adapter = routeAdapter

    }


    override fun onStop() {
        super.onStop()
        if (mFusedLocationClient != null) {

            mFusedLocationClient?.removeLocationUpdates(locationCallback)
        }
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
}
