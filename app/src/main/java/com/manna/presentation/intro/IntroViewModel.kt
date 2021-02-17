package com.manna.presentation.intro

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.manna.common.BaseViewModel
import com.manna.common.Logger
import com.manna.data.source.repo.MeetRepository
import com.manna.common.plusAssign
import com.manna.network.api.MeetApi
import com.manna.util.UserHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class IntroViewModel @ViewModelInject constructor(
    private val repository: MeetRepository,
    private val meetApi: MeetApi
) :
    BaseViewModel() {

    private val _isValidDevice = MutableLiveData<com.manna.common.Event<Boolean>>()
    val isValidDevice: LiveData<com.manna.common.Event<Boolean>> get() = _isValidDevice

    fun checkDevice(deviceId: String) {
        compositeDisposable += repository.getUser(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.deviceId != null && it.username != null) {
                    UserHolder.userResponse = it
                    _isValidDevice.value = com.manna.common.Event(true)
                } else {
                    _isValidDevice.value = com.manna.common.Event(false)
                }
            }, {
                Logger.d("$it")
                _isValidDevice.value = com.manna.common.Event(false)
            })
    }

    fun registerDevice(userName: String, deviceId: String) {
        compositeDisposable += repository.registerUser(userName, deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.deviceId != null && it.username != null) {
                    UserHolder.userResponse = it
                    registerPushToken(deviceId)
                } else {
                    _isValidDevice.value = com.manna.common.Event(false)
                }
            }, {
                Logger.d("$it")
                _isValidDevice.value = com.manna.common.Event(false)
            })
    }

    private fun registerPushToken(deviceId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.d("${task.exception}")
                _isValidDevice.value = com.manna.common.Event(false)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result ?: return@OnCompleteListener

            compositeDisposable += meetApi.registerPushToken(deviceId, token, "fcms")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.d("$it")
                    _isValidDevice.value = com.manna.common.Event(true)
                }, {
                    Logger.d("$it")
                    _isValidDevice.value = com.manna.common.Event(false)
                })

        })
    }
}