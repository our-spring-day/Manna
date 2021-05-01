package com.manna.fcm

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manna.MannaApp
import com.manna.common.Logger
import com.manna.di.ApiModule
import com.manna.util.DeviceUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.d("${task.exception}")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result ?: return@OnCompleteListener
            val deviceId = DeviceUtil.getAndroidID(MannaApp.context())

            ApiModule.provideMeetApi().registerPushToken(deviceId, token, "fcms")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.d("$it")
                }, {
                    Logger.d("$it")
                })
        })
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Logger.d("${remoteMessage.notification?.title}")
        Logger.d("${remoteMessage.notification?.body}")
        Logger.d("${remoteMessage.notification?.channelId}")

//        Logger.d("${remoteMessage.data["Push Test"]}")
//        Logger.d("${remoteMessage.data.values.toList()}")


        if (remoteMessage.data.isNotEmpty()) {
            Logger.d("Message data payload: ${remoteMessage.data}")
//            MyNotificationManager().show(applicationContext, NOTIFICATION_ID++, "Where Are You", remoteMessage.data.values.toList().getOrNull(0) ?: "")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            MyNotificationManager().show(applicationContext, NOTIFICATION_ID++, it)
        }
    }

    companion object {
        private var NOTIFICATION_ID = 0
    }
}