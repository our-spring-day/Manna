package com.manna.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings


object DeviceUtil {

    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
