package com.manna.util

import com.manna.network.model.meet.UserResponse

object UserHolder {
    var userResponse: UserResponse? = null
    val deviceId: String
        get() = userResponse?.deviceId.orEmpty()
}