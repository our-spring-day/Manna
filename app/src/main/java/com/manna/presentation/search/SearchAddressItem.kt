package com.manna.presentation.search

import com.manna.network.model.search_address.SearchAddress

data class SearchAddressItem(
    val addressName: String,
    val roadAddressName: String,
    val placeName: String,
    val latitude: String,
    val longitude: String,
    val onClick: (SearchAddressItem)-> Unit,
    val onMapClick: (SearchAddressItem)-> Unit
){

    companion object{
        fun of(searchAddress: SearchAddress, onClick: (SearchAddressItem) -> Unit, onMapClick: (SearchAddressItem) -> Unit): SearchAddressItem =
            SearchAddressItem(
                addressName = searchAddress.addressName,
                roadAddressName = searchAddress.roadAddressName,
                placeName = searchAddress.placeName,
                latitude = searchAddress.y,
                longitude = searchAddress.x,
                onClick = onClick,
                onMapClick = onMapClick
            )
    }


}
