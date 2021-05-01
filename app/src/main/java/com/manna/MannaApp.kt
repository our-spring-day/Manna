package com.manna

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MannaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        NaverMapSdk.getInstance(this).client =
            NaverCloudPlatformClient(BuildConfig.NAVER_CLIENT_ID)
        KakaoSdk.init(this, BuildConfig.KAKAO_SDK_KEY)
    }

    companion object {
        private lateinit var instance: MannaApp

        fun context(): Context =
            instance.applicationContext
    }
}