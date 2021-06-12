package com.manna.data.source.remote

import androidx.paging.PagingData
import com.manna.network.model.coord_address.CoordAddressResponse
import com.manna.network.model.search_address.SearchAddress
import io.reactivex.Flowable
import io.reactivex.Single

interface AddressRemoteDataSource {

    fun getAddress(latitude: Double, longitude: Double): Single<CoordAddressResponse>

    fun getAddressByKeyword(keyword: String, latitude: Double, longitude: Double): Flowable<PagingData<SearchAddress>>

}