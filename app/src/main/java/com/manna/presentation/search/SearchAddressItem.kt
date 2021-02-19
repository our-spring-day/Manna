package com.manna.presentation.search

import android.os.Parcelable
import com.manna.network.model.search_address.SearchAddress
import kotlinx.android.parcel.Parcelize

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

data class SearchAddressItem(
    val addressName: String,
    val keyWord: String,
    val placeName: String,
    val latitude: String,
    val longitude: String,
    val onClick: (SearchAddressItem) -> Unit,
    val onMapClick: (SearchAddressItem) -> Unit
) {

    companion object {
        fun of(
            searchAddress: SearchAddress,
            keyWord: String,
            onClick: (SearchAddressItem) -> Unit,
            onMapClick: (SearchAddressItem) -> Unit
        ): SearchAddressItem =
            SearchAddressItem(
                addressName = if (!searchAddress.roadAddressName.isNullOrEmpty()) searchAddress.roadAddressName else searchAddress.addressName,
                keyWord = keyWord,
                placeName = searchAddress.placeName,
                latitude = searchAddress.y,
                longitude = searchAddress.x,
                onClick = onClick,
                onMapClick = onMapClick
            )
    }
}
