package com.manna.ext

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibleIf")
fun View.setVisibleIf(value: Boolean) {
    isVisible = value
}

@BindingAdapter("android:invisibleIf")
fun View.setInvisibleIf(value: Boolean) {
    isInvisible = value
}

@BindingAdapter("android:goneIf")
fun View.setGoneIf(value: Boolean) {
    isGone = value
}
