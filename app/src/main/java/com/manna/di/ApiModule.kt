package com.manna.di

import com.manna.network.api.AddressApi
import com.manna.network.api.BingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideAddressApi(): AddressApi {
        return Retrofit.Builder()
            .baseUrl(AddressApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(AddressApi::class.java)
    }


    @Singleton
    @Provides
    fun provideBingApi(): BingApi {
        return Retrofit.Builder()
            .baseUrl(BingApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(BingApi::class.java)
    }
}