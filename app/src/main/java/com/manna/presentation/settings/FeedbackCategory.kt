package com.manna.presentation.settings

import com.manna.R

enum class FeedbackCategory(val category: String, val title: Int, val message: Int) {
    ERROR("error", R.string.error_report, R.string.toast_error_report),
    FEEDBACK("feedback", R.string.feedback, R.string.toast_feedback),
    INQUIRY("inquiry", R.string.inquiry, R.string.toast_inquiry)
}