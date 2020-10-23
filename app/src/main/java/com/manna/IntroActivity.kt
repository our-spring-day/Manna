package com.manna

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manna.data.source.repo.MeetRepository
import com.manna.ext.plusAssign
import com.manna.network.model.meet.UserResponse
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


object UserHolder {
    var userResponse: UserResponse? = null
}

class IntroViewModel @ViewModelInject constructor(private val repository: MeetRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _isValidDevice = MutableLiveData<Event<Boolean>>()
    val isValidDevice: LiveData<Event<Boolean>> get() = _isValidDevice

    fun checkDevice(deviceId: String) {
        compositeDisposable += repository.getUser(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.deviceId != null && it.username != null) {
                    UserHolder.userResponse = it
                    _isValidDevice.value = Event(true)
                } else {
                    registerDevice(deviceId)
                }
            }, {
                Logger.d("$it")
                registerDevice(deviceId)
            })
    }

    private fun registerDevice(deviceId: String) {
        compositeDisposable += repository.registerUser("원우석", deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.deviceId != null && it.username != null) {
                    UserHolder.userResponse = it
                    _isValidDevice.value = Event(true)
                } else {
                    _isValidDevice.value = Event(false)
                }
            }, {
                Logger.d("$it")
                _isValidDevice.value = Event(false)
            })
    }
}


@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewModel.checkDevice(DeviceUtil.getAndroidID(this))

        viewModel.isValidDevice.observe(this, EventObserver { isValid ->
            if (isValid) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "기기 인증이 안되네요 허허", Toast.LENGTH_SHORT).show()
            }
        })

    }
}