package com.manna.di

import com.manna.data.source.remote.AddressRemoteDataSource
import com.manna.data.source.remote.AddressRemoteDataSourceImpl
import com.manna.data.source.remote.MeetRemoteDataSource
import com.manna.data.source.remote.MeetRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SourceModule {

    @Singleton
    @Binds
    abstract fun bindAddressRemoteSource(
        addressRemoteDataSourceImpl: AddressRemoteDataSourceImpl
    ): AddressRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindMeetRemoteSource(
        meetRemoteDataSourceImpl: MeetRemoteDataSourceImpl
    ): MeetRemoteDataSource
}