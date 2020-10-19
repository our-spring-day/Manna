package com.manna

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.manna.data.source.repo.MeetRepository
import com.manna.ext.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MeetListViewModel @ViewModelInject constructor(private val repository: MeetRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun getMeetList(deviceId: String) {
        compositeDisposable += repository.getMeetList(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            })
    }
}
