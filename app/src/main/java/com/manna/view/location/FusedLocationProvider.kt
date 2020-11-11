package com.manna.view.location

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class FusedLocationProvider(activity: Activity, private val callback: (Location) -> Unit) {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val locationRequest by lazy {
        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS)
    }


    init {
        initFusedLocationClient(activity)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val locationList: List<Location> = locationResult.locations
            locationList.getOrNull(locationList.lastIndex)
                ?.let { location ->
                    callback(location)
                }
        }
    }

    private fun initFusedLocationClient(activity: Activity) {
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    @SuppressLint("MissingPermission")
    fun enableLocationCallback() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun disableLocationCallback() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    companion object {
        private const val UPDATE_INTERVAL_MS = 2000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 2000L
    }
}
