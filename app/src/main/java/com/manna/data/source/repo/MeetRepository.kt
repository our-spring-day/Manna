package com.manna.data.source.repo

import com.manna.network.model.meet.MeetResponse
import com.manna.network.model.meet.MeetResponseItem
import com.manna.network.model.meet.UserResponse
import io.reactivex.Single

interface MeetRepository {

    fun getUser(deviceId: String): Single<UserResponse>

    fun registerUser(userName: String, deviceId: String): Single<UserResponse>

    fun getMeetList(deviceId: String): Single<MeetResponse>

    fun registerMeet(meetName: String, deviceId: String): Single<MeetResponseItem>
}

