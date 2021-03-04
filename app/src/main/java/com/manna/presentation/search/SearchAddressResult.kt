package com.manna.presentation.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchAddressResult(
    val addressName: String,
    val keyWord: String,
    val placeName: String,
    val latitude: String,
    val longitude: String
): Parcelable {
    companion object {
        fun of(item: SearchAddressItem): SearchAddressResult =
            SearchAddressResult(item.addressName, item.keyWord, item.placeName, item.latitude, item.longitude)
    }
}