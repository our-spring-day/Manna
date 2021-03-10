package com.manna.presentation.make_meet

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.manna.common.BaseViewModel
import com.manna.presentation.search.SearchAddressResult
import java.util.*

class MeetRegisterViewModel @ViewModelInject constructor() : BaseViewModel() {

    val date = MutableLiveData<Date>()
    val addressItem = MutableLiveData<SearchAddressResult>()
    val participantCount = MutableLiveData<Int>()
    val memo = MutableLiveData<String>()
    val penalty = MutableLiveData<Penalty>()

}