package com.manna.data.source.remote

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.manna.BuildConfig
import com.manna.common.Logger
import com.manna.di.ApiModule
import com.manna.network.api.AddressApi
import com.manna.network.model.search_address.SearchAddress
import com.manna.network.model.search_address.SearchAddressResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchAddressPagingSource(
    private val addressApi: AddressApi = ApiModule.provideAddressApi(),
    private val keyword: String,
) : RxPagingSource<Int, SearchAddress>() {

    override fun getRefreshKey(state: PagingState<Int, SearchAddress>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, SearchAddress>> {
        val nextPage = params.key ?: 1
        return addressApi.getAddressByKeyword(
            "KakaoAK ${BuildConfig.KAKAO_REST_KEY}",
            keyword = keyword,
            page = nextPage,
            size = 15
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                toLoadResult(it, nextPage, it.meta.isEnd)
            }
            .onErrorReturn {
                LoadResult.Error(it)
            }
    }

    private fun toLoadResult(
        searchAddressResponse: SearchAddressResponse,
        key: Int,
        isEnd: Boolean,
    ): LoadResult<Int, SearchAddress> {
        val list = searchAddressResponse.searchAddresses
        Logger.d("$list")
        return LoadResult.Page(
            data = list,
            prevKey = null, // Only paging forward.
            nextKey = if (isEnd) null else key + 1
        )
    }
}