package com.manna

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.manna.common.Logger
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MannaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        NaverMapSdk.getInstance(this).client =
            NaverCloudPlatformClient(getString(R.string.naver_client_id))
        KakaoSdk.init(this, "b4851266cf226144d1a0f2e6f4002167")

        var keyHash = Utility.getKeyHash(this)
        Logger.d("keyHash $keyHash")


    }

    companion object {
        private lateinit var instance: MannaApp

        fun context(): Context =
            instance.applicationContext
    }
}