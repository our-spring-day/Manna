package com.manna.data.source.remote

import com.manna.network.model.coord_address.CoordAddressResponse
import com.manna.network.model.search_address.SearchAddressResponse
import io.reactivex.Single

interface AddressRemoteDataSource {

    fun getAddress(latitude: Double, longitude: Double): Single<CoordAddressResponse>

    fun getAddressByKeyword(keyword: String, latitude: Double, longitude: Double): Single<SearchAddressResponse>

}