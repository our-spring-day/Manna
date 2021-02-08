package com.manna.presentation.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.manna.common.BaseViewModel

class SettingsViewModel @ViewModelInject constructor() :
    BaseViewModel() {

    val onClick: (FeedbackCategory) -> Unit = {
        clickItem.value = it
    }

    val clickItem = MutableLiveData<FeedbackCategory>()
}