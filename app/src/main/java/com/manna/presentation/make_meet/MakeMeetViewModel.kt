package com.manna.presentation.make_meet

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.manna.common.BaseViewModel
import com.manna.common.Event
import com.manna.presentation.search.SearchAddressResult
import java.util.*

class MakeMeetViewModel @ViewModelInject constructor() : BaseViewModel() {

    val date = MutableLiveData<Date>()
    val addressItem = MutableLiveData<SearchAddressResult>()
    val participantCount = MutableLiveData<Int>()
    val memo = MutableLiveData<String>()
    val penalty = MutableLiveData<Penalty>()

    private val _success = MutableLiveData<Event<Unit>>()
    val success: LiveData<Event<Unit>>
        get() = _success

    fun make() {
        _success.value = Event(Unit)
    }

}