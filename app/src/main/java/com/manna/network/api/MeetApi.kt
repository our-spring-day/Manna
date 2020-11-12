package com.manna.network.api

import com.google.gson.JsonObject
import com.manna.network.model.chat.ChatListResponse
import com.manna.network.model.meet.MeetResponse
import com.manna.network.model.meet.MeetResponseItem
import com.manna.network.model.meet.UserResponse
import io.reactivex.Single
import retrofit2.http.*

interface MeetApi {


    @GET("user")
    fun getUser(@Query("device_id") deviceId: String): Single<UserResponse>

    @POST("user")
    fun registerUser(@Body body: JsonObject): Single<UserResponse>

    @GET("manna")
    fun getMeetList(@Query("deviceToken") deviceId: String): Single<MeetResponse>

    @POST("manna")
    fun registerMeet(@Body body: JsonObject): Single<MeetResponseItem>

    @GET("manna/{uuid}/chat")
    fun getChatList(
        @Path("uuid") roomId: String,
        @Query("deviceToken") deviceId: String
    ): Single<ChatListResponse>

    companion object {
        const val BASE_URL = "https://manna.duckdns.org:18888/"
        const val SOCKET_URL = "https://manna.duckdns.org:19999/"
    }
}