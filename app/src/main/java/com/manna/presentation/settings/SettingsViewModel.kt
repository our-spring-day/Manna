package com.manna.presentation.settings

import androidx.lifecycle.MutableLiveData
import com.manna.common.BaseViewModel
import com.manna.data.model.NoticeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() :
    BaseViewModel() {

    val onNoticeClick: (NoticeItem) -> Unit = {
        clickNoticeItem.value = it
    }

    val clickNoticeItem = MutableLiveData<NoticeItem>()
}