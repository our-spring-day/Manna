package com.manna.presentation.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.manna.common.BaseViewModel
import com.manna.data.source.repo.AddressRepository
import com.manna.ext.plusAssign
import com.manna.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchViewModel @ViewModelInject constructor(private val addressRepository: AddressRepository) :
    BaseViewModel() {


    private val onClick: (SearchAddressItem) -> Unit = {
        clickItem.value = it
    }
    private val onMapClick: (SearchAddressItem) -> Unit = {


//        startActivity(
//            SearchDetailActivity.getIntent(
//                context = this,
//                latitude = it.latitude.toDouble(),
//                longitude = it.longitude.toDouble()
//            )
//        )
    }

    val clickItem = MutableLiveData<SearchAddressItem>()

    private val _addressItems = MutableLiveData<List<SearchAddressItem>>()
    val addressItems: LiveData<List<SearchAddressItem>> get() = _addressItems


    fun search(keyWord: String) {
        Logger.d("$keyWord")
        compositeDisposable += addressRepository.getAddressByKeyword(keyWord, 0.0, 0.0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Logger.d("${it.searchAddresses}")


                val list = it.searchAddresses.map {
                    SearchAddressItem.of(it, onClick, onMapClick)
                }
                _addressItems.value = list
            }, {
                Logger.d("$it")
            })
    }


}