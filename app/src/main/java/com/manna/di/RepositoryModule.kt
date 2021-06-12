package com.manna.di

import com.manna.data.source.repo.AddressRepository
import com.manna.data.source.repo.AddressRepositoryImpl
import com.manna.data.source.repo.MeetRepository
import com.manna.data.source.repo.MeetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAddressRepo(
        addressRepositoryImpl: AddressRepositoryImpl
    ): AddressRepository

    @Singleton
    @Binds
    abstract fun bindMeetRepo(
        meetRepositoryImpl: MeetRepositoryImpl
    ): MeetRepository
}