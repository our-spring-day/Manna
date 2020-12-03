package com.manna.presentation.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.manna.R

class ChatAdapter :
    ListAdapter<ChatItem, ChatAdapterViewHolder>(
        object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean =
                oldItem.timeStamp == newItem.timeStamp && oldItem.message == newItem.message


            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean =
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

    override fun getItemViewType(position: Int): Int =
        when (currentList[position].type) {
            ChatItem.Type.MY_CHAT -> MY_CHAT
            ChatItem.Type.CHAT -> CHAT
        }

    companion object {
        private const val CHAT = 0
        private const val MY_CHAT = 1
    }
}