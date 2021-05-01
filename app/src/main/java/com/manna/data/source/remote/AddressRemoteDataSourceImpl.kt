package com.manna.data.source.remote

import com.manna.BuildConfig
import com.manna.network.api.AddressApi
import com.manna.network.model.coord_address.CoordAddressResponse
import com.manna.network.model.search_address.SearchAddressResponse
import io.reactivex.Single
import javax.inject.Inject

class AddressRemoteDataSourceImpl @Inject constructor(private val addressApi: AddressApi) : AddressRemoteDataSource {

    private val authorization = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"

    override fun getAddress(latitude: Double, longitude: Double): Single<CoordAddressResponse> {
        return addressApi.getCoordAddress(authorization, latitude, longitude)
    }

    override fun getAddressByKeyword(
        keyword: String,
        latitude: Double,
        longitude: Double
    ): Single<SearchAddressResponse> {
        return addressApi.getAddressByKeyword(authorization, keyword)
    }
}