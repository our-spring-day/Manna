package com.manna.network.api

import com.google.gson.JsonObject
import com.manna.network.model.meet.MeetResponse
import com.manna.network.model.meet.MeetResponseItem
import com.manna.network.model.meet.UserResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MeetApi {


    @GET("user")
    fun getUser(@Query("device_id") deviceId: String): Single<UserResponse>

    @POST("user")
    fun registerUser(@Body body: JsonObject): Single<UserResponse>

    @GET("manna")
    fun getMeetList(@Query("device_id") deviceId: String): Single<MeetResponse>

    @POST("manna")
    fun registerMeet(@Body body: JsonObject): Single<MeetResponseItem>

    companion object {
        const val BASE_URL = "http://ec2-13-124-151-24.ap-northeast-2.compute.amazonaws.com:8888/"
    }
}