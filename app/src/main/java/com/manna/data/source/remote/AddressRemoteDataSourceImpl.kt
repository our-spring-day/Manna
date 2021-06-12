package com.manna.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.manna.BuildConfig
import com.manna.network.api.AddressApi
import com.manna.network.model.coord_address.CoordAddressResponse
import com.manna.network.model.search_address.SearchAddress
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class AddressRemoteDataSourceImpl @Inject constructor(
    private val addressApi: AddressApi,
) : AddressRemoteDataSource {

    private val authorization = "KakaoAK ${BuildConfig.KAKAO_REST_KEY}"

    override fun getAddress(latitude: Double, longitude: Double): Single<CoordAddressResponse> {
        return addressApi.getCoordAddress(authorization, latitude, longitude)
    }

    override fun getAddressByKeyword(
        keyword: String,
        latitude: Double,
        longitude: Double,
    ): Flowable<PagingData<SearchAddress>> {
        val pagingSource = SearchAddressPagingSource(keyword = keyword)

        val pager = Pager(
            PagingConfig(15, initialLoadSize = 15, prefetchDistance = 1, enablePlaceholders = true),
            pagingSourceFactory = {
                pagingSource
            }
        )
        return pager.flowable
    }
}