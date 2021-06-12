package com.manna.data.source.repo

import androidx.paging.PagingData
import com.manna.data.source.remote.AddressRemoteDataSource
import com.manna.network.model.coord_address.CoordAddressResponse
import com.manna.network.model.search_address.SearchAddress
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(private val remoteDataSource: AddressRemoteDataSource) :
    AddressRepository {
    override fun getAddress(latitude: Double, longitude: Double): Single<CoordAddressResponse> =
        remoteDataSource.getAddress(latitude, longitude)


    override fun getAddressByKeyword(
        keyword: String,
        latitude: Double,
        longitude: Double
    ): Flowable<PagingData<SearchAddress>> =
        remoteDataSource.getAddressByKeyword(keyword, latitude, longitude)
}