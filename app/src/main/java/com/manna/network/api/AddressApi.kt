package com.manna.network.api

import com.manna.network.model.coord_address.CoordAddressResponse
import com.manna.network.model.search_address.SearchAddressResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AddressApi {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
    }

    @GET("v2/local/geo/coord2address.json")
    fun getCoordAddress(
        @Header("Authorization") apiKey: String,
        @Query("y") latitude: Double,
        @Query("x") longitude: Double,
        @Query("input_coord") inputCoord: String = "WGS84"
    ): Single<CoordAddressResponse>


    @GET("v2/local/search/keyword.json")
    fun getAddressByKeyword(
        @Header("Authorization") apiKey: String,
        @Query("query") keyword: String,
//        @Query("y") latitude: Double,
//        @Query("x") longitude: Double,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "accuracy",
    ): Single<SearchAddressResponse>

}