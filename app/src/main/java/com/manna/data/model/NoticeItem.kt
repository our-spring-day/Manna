package com.manna.data.model

import com.manna.presentation.settings.FeedbackCategory

data class NoticeItem(
    val title: String,
    val date: String,
    var onClick: (NoticeItem) -> Unit
)