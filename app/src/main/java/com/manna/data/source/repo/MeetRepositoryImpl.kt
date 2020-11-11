package com.manna.data.source.repo

import com.manna.data.source.remote.MeetRemoteDataSource
import com.manna.network.model.meet.MeetResponse
import com.manna.network.model.meet.MeetResponseItem
import com.manna.network.model.meet.UserResponse
import io.reactivex.Single
import javax.inject.Inject

class MeetRepositoryImpl @Inject constructor(private val meetRemoteDataSource: MeetRemoteDataSource) : MeetRepository {

    override fun getUser(deviceId: String): Single<UserResponse> {
        return meetRemoteDataSource.getUser(deviceId)
    }

    override fun registerUser(userName: String, deviceId: String): Single<UserResponse> {
        return meetRemoteDataSource.registerUser(userName, deviceId)
    }

    override fun getMeetList(deviceId: String): Single<MeetResponse> {
        return meetRemoteDataSource.getMeetList(deviceId)
    }

    override fun registerMeet(meetName: String, deviceId: String): Single<MeetResponseItem> {
        return meetRemoteDataSource.registerMeet(meetName, deviceId)
    }
}