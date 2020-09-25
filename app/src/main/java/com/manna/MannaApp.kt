package com.manna

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MannaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client = NaverCloudPlatformClient(getString(R.string.naver_client_id))
    }
}