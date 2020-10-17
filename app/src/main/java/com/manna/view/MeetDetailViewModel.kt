package com.manna.view

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.manna.data.source.repo.AddressRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MeetDetailViewModel @ViewModelInject constructor(private val repository: AddressRepository) :
    ViewModel() {


    fun getPlaceDetail() {
//        repository.getAddress(37.5129949, 127.1005435)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                Log.d("TEST", "$it")
//            }, {
//                Log.d("TEST", "$it")
//            })
    }


}