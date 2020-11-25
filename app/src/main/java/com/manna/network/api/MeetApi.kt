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
    fun getUser(@Query("deviceToken") deviceToken: String): Single<UserResponse>

    @POST("user")
    fun registerUser(@Body body: JsonObject): Single<UserResponse>

    @POST("user/pushToken")
    fun registerPushToken(
        @Query("deviceToken") deviceToken: String,
        @Body body: JsonObject
    ): Single<JsonObject>


    @GET("manna")
    fun getMeetList(@Query("deviceToken") deviceToken: String): Single<MeetResponse>

    @POST("manna")
    fun registerMeet(
        @Body body: JsonObject,
        @Query("deviceToken") deviceToken: String
    ): Single<MeetResponseItem>

    @GET("manna/{uuid}/chat")
    fun getChatList(
        @Path("uuid") roomId: String,
        @Query("deviceToken") deviceToken: String
    ): Single<ChatListResponse>

    @POST("manna/{uuid}/push/{userToken}")
    fun sendPushMessage(
        @Path("uuid") roomId: String,
        @Path("userToken") receiverToken: String,
        @Query("deviceToken") deviceToken: String
    ): Single<JsonObject>

    companion object {
        const val BASE_URL = "https://manna.duckdns.org:18888/"
        const val SOCKET_URL = "https://manna.duckdns.org:19999/"
    }
}