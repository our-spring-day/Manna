package com.manna.data.source.di

import com.manna.data.source.remote.AddressRemoteDataSource
import com.manna.data.source.remote.AddressRemoteDataSourceImpl
import org.koin.dsl.module

val sourceModule = module {
    single<AddressRemoteDataSource> { AddressRemoteDataSourceImpl(get()) }
}