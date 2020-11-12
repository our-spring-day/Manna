package com.manna.view.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.manna.Logger
import com.manna.R
import com.manna.UserHolder
import com.manna.databinding.FragmentChatBinding
import com.manna.di.ApiModule
import com.manna.ext.HeightProvider
import com.manna.ext.ViewUtil
import com.manna.network.api.MeetApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private var keyboardHeight = 0
    private var chatSocket: Socket? = null
    private lateinit var chatAdapter: ChatAdapter

    private val onChatConnectReceiver = Emitter.Listener { args ->
        activity?.runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }

    private val onChatReceiver = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val response = args.getOrNull(0)

            val chatResponse = Gson().fromJson(response.toString(), ChatResponse::class.java)
            Logger.d("chatResponse: $chatResponse")

            if (chatResponse.type == ChatResponse.Type.CHAT) {
                val chatType =
                    if (chatResponse.sender?.deviceToken == UserHolder.userResponse?.deviceId) ChatItem.Type.MY_CHAT else ChatItem.Type.CHAT
                val chatItem = ChatItem(
                    message = chatResponse.message?.message.orEmpty(),
                    name = chatResponse.sender?.username.orEmpty(),
                    timeStamp = chatResponse.message?.createTimestamp ?: -1L,
                    type = chatType,
                    deviceToken = chatResponse.sender?.deviceToken.orEmpty()
                )

                chatAdapter.submitList(chatAdapter.currentList + chatItem) {
                    setChatViewScrollEnd()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val roomId = arguments?.getString(ARG_ROOM_ID).orEmpty()
        connect(roomId)

        ApiModule.provideMeetApi()
            .getChatList(roomId, UserHolder.userResponse?.deviceId.orEmpty())
            .map { chatListResponse ->
                chatListResponse.sortBy { it.createTimestamp }

                chatListResponse.mapIndexed { index, chatListResponseItem ->
                    val chatType =
                        if (chatListResponseItem.sender?.deviceToken == UserHolder.userResponse?.deviceId) ChatItem.Type.MY_CHAT else ChatItem.Type.CHAT


                    val deviceToken = if (index == 0) {
                        chatListResponseItem.sender?.deviceToken.orEmpty()
                    } else {
                        val prevItem = chatListResponse[index - 1]
                        val prevDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                            .format(prevItem.createTimestamp)
                        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                            .format(chatListResponseItem.createTimestamp)
                        if (
                            prevItem.sender?.deviceToken != chatListResponseItem.sender?.deviceToken
                            || prevDate != currentDate
                        ) {
                            chatListResponseItem.sender?.deviceToken.orEmpty()
                        } else {
                            ""
                        }
                    }

                    ChatItem(
                        message = chatListResponseItem.message.orEmpty(),
                        name = chatListResponseItem.sender?.username.orEmpty(),
                        timeStamp = chatListResponseItem.createTimestamp ?: -1L,
                        type = chatType,
                        deviceToken = deviceToken
                    )
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chatAdapter.submitList(it) {
                    setChatViewScrollEnd()
                }
            }, {
                Logger.d("$it")
            })



        binding.ddaBong.setOnClickListener {
            val chat = binding.inputChat.text.toString()
            if (chat.isNotEmpty()) {
                chatSocket?.emit(CHAT_MESSAGE, chat)
                binding.inputChat.text.clear()
            }
        }

        binding.chatView.run {
            setPadding(
                paddingStart,
                ViewUtil.getStatusBarHeight(context),
                paddingRight,
                paddingBottom
            )
            layoutManager = LinearLayoutManager(requireContext())
            chatAdapter = ChatAdapter()
            adapter = chatAdapter
        }




        HeightProvider(requireActivity()).init()
            .setHeightListener { height ->
                keyboardHeight = height

                val keyboardTop =
                    ViewUtil.getScreenHeightPixels(activity) -
                        height +
                        ViewUtil.getStatusBarHeight(context) -
                        if (height == 0) binding.bottomView.height else 0

                binding.chatView.layoutParams.height =
                    keyboardTop - binding.chatInputView.height
                binding.chatView.requestLayout()

                inputViewTransY(keyboardTop)

                if (height == 0) {
                    binding.inputChat.clearFocus()
                } else {
                    setChatViewScrollEnd()
                }
            }
    }

    private fun setChatViewScrollEnd() {
        binding.chatView.smoothScrollToPosition(chatAdapter.itemCount)
    }

    private fun inputViewTransY(y: Int) {
        binding.chatInputView.animate()
            .y((y - binding.chatInputView.height).toFloat())
            .setDuration(0)
            .start()
    }

    private fun connect(roomId: String) {
        if (chatSocket?.connected() == true) return

        val options = IO.Options().apply {
            if (Logger.socketLogging) {
                val interceptor = HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }

                val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
                callFactory = client
                webSocketFactory = client
            }
            query = "mannaID=${roomId}&deviceToken=${UserHolder.userResponse?.deviceId}"
        }

        val chatManager = Manager(URI(MeetApi.SOCKET_URL), options)

        chatSocket =
            chatManager.socket("/chat").apply {
                on(CHAT_CONNECT, onChatConnectReceiver)
                on(CHAT_MESSAGE, onChatReceiver)

                on(Socket.EVENT_CONNECT) {
                    Logger.d("EVENT_CONNECT ${it.map { it.toString() }}")
                }

                on(Socket.EVENT_DISCONNECT) {
                    Logger.d("EVENT_DISCONNECT ${it.map { it.toString() }}")
                }

                on(Socket.EVENT_MESSAGE) {
                    Logger.d("EVENT_MESSAGE ${it.map { it.toString() }}")
                }

                connect()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        chatSocket?.disconnect()
        chatSocket?.off(CHAT_CONNECT, onChatConnectReceiver)
        chatSocket?.off(CHAT_MESSAGE, onChatReceiver)
    }

    companion object {
        private const val CHAT_CONNECT = "chatConnect"
        private const val CHAT_MESSAGE = "chat"
        private const val ARG_ROOM_ID = "room_id"

        fun newInstance(roomId: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ROOM_ID, roomId)
            }
        }
    }
}