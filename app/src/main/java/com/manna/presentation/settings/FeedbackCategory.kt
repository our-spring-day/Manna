package com.manna.presentation.settings

data class FeedbackCategory(
    val category: String,
    var click: Boolean,
    var onClick: (FeedbackCategory) -> Unit
)