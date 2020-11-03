package com.manna.network.api

import com.manna.network.model.test.RootResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*


/**
 * https://dev.virtualearth.net/rest/v1/routes/transit?
 * wp.0=37.482087,126.976742
 * & wp.1=37.479846,126.958683
 * & timetype=departure
 * & datetime=3:00:00pm
 * & output=json
 * & key=Ao2GRV09K_rtjtR8UpkI95is2ItgRHDHENkjy-Fg0CfDUZ7R20bRBKXeG6hsq2Ek
 */

interface BingApi {

    companion object {
        const val BASE_URL = "https://dev.virtualearth.net/"
    }

    @GET("rest/v1/routes/Transit")
    fun getRoute(
        @Query("wp.0") startLatLng: String,
        @Query("wp.1") endLatLng: String,
        @Query("timetype") timeType: String = "departure",
        @Query("datetime") datetime: String = SimpleDateFormat("hh:mm:ss", Locale.KOREA).format(Date()),
        @Query("key") key: String = "Ao2GRV09K_rtjtR8UpkI95is2ItgRHDHENkjy-Fg0CfDUZ7R20bRBKXeG6hsq2Ek",
        @Query("culture") culture: String = "ko",
        @Query("routeAttributes") routeAttributes: String = "routePath,transitStops",
        @Query("distanceUnit") distanceUnit: String = "km"
    ): Observable<RootResponse>

    @GET("rest/v1/routes/Walking")
    fun getRouteWalking(
        @Query("wp.0") startLatLng: String,
        @Query("wp.1") endLatLng: String,
        @Query("optmz") optmz: String = "distance",
        @Query("key") key: String = "Ao2GRV09K_rtjtR8UpkI95is2ItgRHDHENkjy-Fg0CfDUZ7R20bRBKXeG6hsq2Ek",
        @Query("culture") culture: String = "ko"
    ): Observable<RootResponse>


    @GET("rest/v1/routes/Driving")
    fun getRouteDriving(
        @Query("wp.0") startLatLng: String,
        @Query("wp.1") endLatLng: String,
        @Query("key") key: String = "Ao2GRV09K_rtjtR8UpkI95is2ItgRHDHENkjy-Fg0CfDUZ7R20bRBKXeG6hsq2Ek",
        @Query("culture") culture: String = "ko"
    ): Observable<RootResponse>

}