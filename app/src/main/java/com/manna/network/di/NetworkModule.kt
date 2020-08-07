package com.manna.network.di

import com.manna.network.api.AddressApi
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single<AddressApi> {
        Retrofit.Builder()
            .baseUrl(AddressApi.BASE_URL)
            .addConverterFactory(get())
            .addCallAdapterFactory(get())
            .build()
            .create(AddressApi::class.java)
    }

    single<Converter.Factory> { GsonConverterFactory.create() }
    single<CallAdapter.Factory> { RxJava2CallAdapterFactory.create() }

}