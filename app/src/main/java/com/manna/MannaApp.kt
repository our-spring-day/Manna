package com.manna

import android.app.Application
import android.content.Context
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.Socket


@HiltAndroidApp
class MannaApp : Application() {



    override fun onCreate() {
        super.onCreate()
        instance = this
        NaverMapSdk.getInstance(this).client =
            NaverCloudPlatformClient(getString(R.string.naver_client_id))
    }

    companion object {
        private lateinit var instance: MannaApp
        var locationSocket: Socket? = null

        fun context(): Context =
            instance.applicationContext
    }
}