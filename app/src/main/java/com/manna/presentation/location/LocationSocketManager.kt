package com.manna.presentation.location

import androidx.annotation.MainThread
import com.google.gson.Gson
import com.manna.LocationResponse
import com.manna.common.Logger
import com.manna.network.api.MeetApi
import com.manna.util.UserHolder
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URI

object LocationSocketManager {

    private var locationSocket: Socket? = null
    private var locationResponseCallback: ((LocationResponse) -> Unit)? = null

    @MainThread
    fun setLocationResponseCallback(callback: (LocationResponse) -> Unit) {
        locationResponseCallback = callback
    }

    fun connect(roomId: String) {
        if (locationSocket?.connected() == true) {
            locationSocket?.run {
                off()
                onLocationEvents(this)
            }
            return
        }

        val options = IO.Options().apply {
            if (Logger.socketLogging) {
                val interceptor = HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }

                val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
                callFactory = client
                webSocketFactory = client
            }
            query = "mannaID=${roomId}&deviceToken=${UserHolder.deviceId}"
        }

        val manager = Manager(URI(MeetApi.SOCKET_URL), options)
        locationSocket =
            manager.socket("/location")?.apply {
                onLocationEvents(this)
            }

        locationSocket?.connect()
    }

    fun sendMessage(key: String, message: String) {
        Logger.d("${locationSocket?.connected()} $message")
        if (locationSocket?.connected() == true) {
            locationSocket?.emit(key, message)
        }
    }

    private fun onLocationEvents(socket: Socket) {
        socket.run {
            on(LOCATION_CONNECT, onLocationConnectReceiver)
            on(LOCATION_MESSAGE, onLocationReceiver)
            on(Socket.EVENT_CONNECT) { args ->
                Logger.d("EVENT_CONNECT ${args.map { "$it" }}")
            }
            on(Socket.EVENT_DISCONNECT) { args ->
                Logger.d("EVENT_DISCONNECT ${args.map { "$it" }}")
            }
        }
    }


    private val onLocationReceiver = Emitter.Listener { args ->

        Logger.d("${args.toList()}")
        val message = args.getOrNull(0)
        val socketResponse = Gson().fromJson(message.toString(), LocationResponse::class.java)
        locationResponseCallback?.invoke(socketResponse)
    }

    private val onLocationConnectReceiver = Emitter.Listener { args ->
        Logger.d("${args.map { it.toString() }}")
    }

    private const val LOCATION_CONNECT = "locationConnect"
    private const val LOCATION_MESSAGE = "location"
}