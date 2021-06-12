package com.manna.util

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ToolbarProvider {
    fun setToolbarTitle(@StringRes titleResId: Int)

    fun setToolbarTitle(title: String?)

    fun setNavigationIcon(@DrawableRes iconResId: Int)

    fun setCustomView(view: View)

    fun setCustomView(view: View, relativeHeight: Boolean)

    fun getCustomView(): View?
}