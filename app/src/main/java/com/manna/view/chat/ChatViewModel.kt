package com.manna.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.manna.Logger
import com.manna.UserHolder
import com.manna.common.BaseViewModel
import com.manna.ext.plusAssign
import com.manna.network.api.MeetApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel(private val meetApi: MeetApi) : BaseViewModel() {


    private val _chatList = MutableLiveData<List<ChatItem>>()
    val chatList: LiveData<List<ChatItem>> get() = _chatList

    fun getLatestMessage(roomId: String) {

        compositeDisposable += meetApi
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
                _chatList.value = it
            }, {
                Logger.d("$it")
            })

    }
}