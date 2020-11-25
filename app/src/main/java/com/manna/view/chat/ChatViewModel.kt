package com.manna.view.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.manna.Logger
import com.manna.UserHolder
import com.manna.common.BaseViewModel
import com.manna.ext.plusAssign
import com.manna.network.api.MeetApi
import com.manna.network.model.chat.ChatListResponseItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel @ViewModelInject constructor(private val meetApi: MeetApi) : BaseViewModel() {


    private val _chatList = MutableLiveData<List<ChatItem>>()
    val chatList: LiveData<List<ChatItem>> get() = _chatList

    fun getLatestMessage(roomId: String) {

        compositeDisposable += meetApi
            .getChatList(roomId, UserHolder.deviceId)
            .map { chatListResponse ->
                chatListResponse.sortBy { it.createTimestamp }

                chatListResponse.mapIndexed { index, chatListResponseItem ->
                    val chatType = getChatType(chatListResponseItem)

                    ChatItem(
                        message = chatListResponseItem.message.orEmpty(),
                        name = chatListResponseItem.sender?.username.orEmpty(),
                        timeStamp = chatListResponseItem.createTimestamp ?: -1L,
                        type = chatType,
                        deviceToken = chatListResponseItem.sender?.deviceToken.orEmpty()
                    )
                }
            }
            .map { chatList ->
                chatList.forEachIndexed { index, chatItem ->
                    when (index) {
                        0 -> {
                            chatItem.hasImage = true
                        }
                        else -> {
                            val prevItem = chatList[index - 1]
                            chatItem.hasImage = isNotContinuousChat(prevItem, chatItem)
                        }
                    }
                }

                chatList
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _chatList.value = it
            }, {
                Logger.d("$it")
            })
    }

    fun addChat(chat: ChatItem) {
        val lastChat = chatList.value?.lastOrNull()
        if (lastChat != null && isNotContinuousChat(lastChat, chat)) {
            chat.hasImage = true
        }
        addChatItem(chat)
    }

    private fun addChatItem(chat: ChatItem) {
        _chatList.value = chatList.value.orEmpty() + chat
    }


    private fun isNotContinuousChat(prevItem: ChatItem, currentItem: ChatItem): Boolean {
        val prevDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
            .format(prevItem.timeStamp)
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
            .format(currentItem.timeStamp)

        return (prevItem.deviceToken != currentItem.deviceToken || prevDate != currentDate)
    }

    private fun getChatType(chatListResponseItem: ChatListResponseItem): ChatItem.Type =
        if (chatListResponseItem.sender?.deviceToken == UserHolder.deviceId) ChatItem.Type.MY_CHAT else ChatItem.Type.CHAT

}