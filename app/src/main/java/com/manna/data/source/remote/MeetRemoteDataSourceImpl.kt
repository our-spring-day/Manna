package com.manna.data.source.remote

import com.google.gson.JsonObject
import com.manna.network.api.MeetApi
import com.manna.network.model.meet.MeetResponse
import com.manna.network.model.meet.MeetResponseItem
import com.manna.network.model.meet.UserResponse
import io.reactivex.Single
import javax.inject.Inject

class MeetRemoteDataSourceImpl @Inject constructor(private val meetApi: MeetApi) : MeetRemoteDataSource {

    override fun getUser(deviceId: String): Single<UserResponse> {
        return meetApi.getUser(deviceId)
    }

    override fun registerUser(userName: String, deviceId: String): Single<UserResponse> {
        val body = JsonObject().apply {
            addProperty("username", userName)
            addProperty("deviceToken", deviceId)
        }

        return meetApi.registerUser(body)
    }

    override fun getMeetList(deviceId: String): Single<MeetResponse> {
        return meetApi.getMeetList(deviceId)
    }

    override fun registerMeet(meetName: String, deviceId: String): Single<MeetResponseItem> {
        val body = JsonObject().apply {
            addProperty("mannaName", meetName)
        }

        return meetApi.registerMeet(body, deviceId)
    }
}