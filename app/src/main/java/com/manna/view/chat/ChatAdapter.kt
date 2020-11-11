package com.manna.view.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.manna.ChatResponse
import com.manna.R
import com.manna.UserHolder

class ChatAdapter :
    ListAdapter<ChatResponse, ChatAdapterViewHolder>(
        object : DiffUtil.ItemCallback<ChatResponse>() {
            override fun areItemsTheSame(oldItem: ChatResponse, newItem: ChatResponse): Boolean =
                oldItem.message?.createTimestamp == newItem.message?.createTimestamp


            override fun areContentsTheSame(oldItem: ChatResponse, newItem: ChatResponse): Boolean =
                oldItem == newItem

        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapterViewHolder =
        when (viewType) {
            CHAT -> ChatAdapterViewHolder.ChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
            )
            MY_CHAT -> ChatAdapterViewHolder.MyChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_my_chat, parent, false)
            )
            else -> error("Invalid viewType")
        }


    override fun onBindViewHolder(holder: ChatAdapterViewHolder, position: Int) {
        when (holder) {
            is ChatAdapterViewHolder.ChatViewHolder -> holder.bind(currentList[position])
            is ChatAdapterViewHolder.MyChatViewHolder -> holder.bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].sender?.deviceToken) {
            UserHolder.userResponse?.deviceId -> MY_CHAT
            else -> CHAT
        }
    }

    companion object {
        private const val CHAT = 0
        private const val MY_CHAT = 1
    }
}