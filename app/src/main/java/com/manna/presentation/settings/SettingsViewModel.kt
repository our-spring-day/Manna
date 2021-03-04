package com.manna.presentation.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.manna.common.BaseViewModel
import com.manna.data.model.NoticeItem

class SettingsViewModel @ViewModelInject constructor() :
    BaseViewModel() {

    val onClick: (FeedbackCategory) -> Unit = {
        clickItem.value = it
    }

    val clickItem = MutableLiveData<FeedbackCategory>()

    val onNoticeClick: (NoticeItem) -> Unit = {
        clickNoticeItem.value = it
    }

    val clickNoticeItem = MutableLiveData<NoticeItem>()
}