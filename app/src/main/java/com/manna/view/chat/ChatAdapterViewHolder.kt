package com.manna.view.chat

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.manna.ChatResponse
import com.manna.Logger
import com.manna.databinding.ItemChatBinding
import com.manna.databinding.ItemMyChatBinding
import java.text.SimpleDateFormat
import java.util.*

sealed class ChatAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class ChatViewHolder(view: View) : ChatAdapterViewHolder(view) {

        private val binding = DataBindingUtil.bind<ItemChatBinding>(itemView)!!

        fun bind(item: ChatResponse) {

            Logger.d("${item.type}")
            when (item.type) {
                ChatResponse.Type.CHAT -> {
                    binding.message.text = item.message?.message.orEmpty()
                    binding.name.text = item.sender?.username.orEmpty()

                    item.message?.createTimestamp?.let {
                        binding.date.text =
                            SimpleDateFormat(
                                "h:mm",
                                Locale.KOREA
                            ).format(item.message?.createTimestamp)
                    }
                }
                ChatResponse.Type.JOIN, ChatResponse.Type.LEAVE -> {

                }
            }
        }
    }

    class MyChatViewHolder(view: View) : ChatAdapterViewHolder(view) {
        private val binding = DataBindingUtil.bind<ItemMyChatBinding>(itemView)!!

        fun bind(item: ChatResponse) {
            when (item.type) {
                ChatResponse.Type.CHAT -> {
                    binding.message.text = item.message?.message.orEmpty()

                    item.message?.createTimestamp?.let {
                        binding.date.text =
                            SimpleDateFormat(
                                "h:mm",
                                Locale.KOREA
                            ).format(item.message?.createTimestamp)
                    }
                }
                ChatResponse.Type.JOIN, ChatResponse.Type.LEAVE -> {

                }
            }

        }
    }
}