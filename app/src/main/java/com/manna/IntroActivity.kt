package com.manna

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.manna.common.BaseActivity
import com.manna.data.source.repo.MeetRepository
import com.manna.databinding.ActivityIntroBinding
import com.manna.ext.Event
import com.manna.ext.EventObserver
import com.manna.ext.plusAssign
import com.manna.network.api.MeetApi
import com.manna.network.model.meet.UserResponse
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


object UserHolder {
    var userResponse: UserResponse? = null
    val deviceId: String
        get() = userResponse?.deviceId.orEmpty()
}

class IntroViewModel @ViewModelInject constructor(
    private val repository: MeetRepository,
    private val meetApi: MeetApi
) :
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
                    _isValidDevice.value = Event(false)
                }
            }, {
                Logger.d("$it")
                _isValidDevice.value = Event(false)
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
                    _isValidDevice.value = Event(false)
                }
            }, {
                Logger.d("$it")
                _isValidDevice.value = Event(false)
            })
    }

    private fun registerPushToken(deviceId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.d("${task.exception}")
                _isValidDevice.value = Event(false)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result ?: return@OnCompleteListener

            compositeDisposable += meetApi.registerPushToken(deviceId, token, "fcms")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.d("$it")
                    _isValidDevice.value = Event(true)
                }, {
                    Logger.d("$it")
                    _isValidDevice.value = Event(false)
                })

        })
    }
}


@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>(R.layout.activity_intro) {

    private val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.checkDevice(DeviceUtil.getAndroidID(this))

        viewModel.isValidDevice.observe(this, EventObserver { isValid ->
            if (isValid) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                val message = if (binding.registerNameGroup.isVisible) {
                    "기기인증이 실패했어요. 다시 시도해보세요."
                } else {
                    "처음 오셨군요? 사용할 닉네임을 입력해주세요"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                binding.registerNameGroup.isVisible = true
            }
        })

        binding.submitName.setOnClickListener {
            val name = binding.inputName.text.toString()
            if (name.isNotEmpty()) {
                viewModel.registerDevice(name, DeviceUtil.getAndroidID(this))
            }
        }

    }
}