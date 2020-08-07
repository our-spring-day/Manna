package com.manna

import android.app.Application
import com.manna.data.source.di.sourceModule
import com.manna.network.di.networkModule
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MannaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client = NaverCloudPlatformClient(getString(R.string.naver_client_id))
        setUpKoin()
    }

    private fun setUpKoin() {
        startKoin {
            androidContext(this@MannaApp)
            modules(
                listOf(
                    networkModule,
                    sourceModule
                )
            )
        }
    }

}