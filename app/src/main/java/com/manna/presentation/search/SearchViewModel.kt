package com.manna.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava2.cachedIn
import com.manna.common.BaseViewModel
import com.manna.common.Logger
import com.manna.common.plusAssign
import com.manna.data.source.repo.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val addressRepository: AddressRepository) :
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

    private val searchSubject: PublishSubject<String> =
        PublishSubject.create()

    init {
        compositeDisposable += searchSubject
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ keyWord ->
                _addressPagingData.value = getFlowable(keyWord).cachedIn(viewModelScope)
            }, {
                Logger.d("$it")
            })
    }

    val clickItem = MutableLiveData<SearchAddressItem>()

    private val _addressPagingData = MutableLiveData<Flowable<PagingData<SearchAddressItem>>>()
    val addressPagingData: LiveData<Flowable<PagingData<SearchAddressItem>>> get() = _addressPagingData


    fun search(keyWord: String) {
        searchSubject.onNext(keyWord)
    }

    private fun getFlowable(keyWord: String): Flowable<PagingData<SearchAddressItem>> {
        return addressRepository.getAddressByKeyword(keyWord, 0.0, 0.0)
            .map {
                it.map { address ->
                    SearchAddressItem.of(address, keyWord, onClick, onMapClick)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}