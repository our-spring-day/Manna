package com.manna.presentation.chat

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manna.R
import com.manna.databinding.ItemChatBinding
import com.manna.databinding.ItemMyChatBinding
import com.manna.util.ViewUtil
import java.text.SimpleDateFormat
import java.util.*

data class ChatItem(
    val message: String,
    val name: String,
    val timeStamp: Long,
    val type: Type,
    val deviceToken: String,
    var hasImage: Boolean = false
) {
    enum class Type {
        MY_CHAT,
        CHAT
    }
}

sealed class ChatAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class ChatViewHolder(view: View) : ChatAdapterViewHolder(view) {
        private val binding = DataBindingUtil.bind<ItemChatBinding>(itemView)!!

        fun bind(item: ChatItem) {
            binding.run {
                val topPadding = if (item.hasImage) 16f else 0f
                root.setPadding(
                    root.paddingStart,
                    ViewUtil.convertDpToPixel(itemView.context, topPadding).toInt(),
                    root.paddingEnd,
                    root.paddingBottom
                )
                message.text = item.message
                name.text = item.name
                date.text = SimpleDateFormat("H:mm", Locale.KOREA).format(item.timeStamp)
                profileImage.isVisible = item.hasImage
                name.isVisible = item.hasImage
                profileImage.setImage("https://imgs.mongabay.com/wp-content/uploads/sites/20/2020/02/13100557/Screen-Shot-2020-02-13-at-9.04.42-AM.png")
//                setImage(profileImage, item.deviceToken)
            }
        }
    }

    class MyChatViewHolder(view: View) : ChatAdapterViewHolder(view) {
        private val binding = DataBindingUtil.bind<ItemMyChatBinding>(itemView)!!

        fun bind(item: ChatItem) {
            binding.run {
                message.text = item.message
                date.text = SimpleDateFormat("H:mm", Locale.KOREA).format(item.timeStamp)
            }
        }
    }


    protected fun setImage(imageView: ImageView, deviceToken: String) {
        kotlin.runCatching {
            val imageResId = when (deviceToken) {
                "aed64e8da3a07df4" -> R.drawable.test_2
                "f606564d8371e455" -> R.drawable.image_3
                "8F630481-548D-4B8A-B501-FFD90ADFDBA4" -> R.drawable.image_2
                "0954A791-B5BE-4B56-8F25-07554A4D6684" -> R.drawable.image_4
                "C65CDF73-8C04-4F76-A26A-AE3400FEC14B" -> R.drawable.image_6
                "69751764-A224-4923-9844-C61646743D10" -> R.drawable.image_1
                "2872483D-9E7B-46D1-A2B8-44832FE3F1AD" -> R.drawable.image_5
                "8D44FAA1-2F87-4702-9DAC-B8B15D949880" -> R.drawable.image_7
                else -> R.drawable.test_1
            }

            Glide.with(imageView.context).load(imageResId).into(imageView)
        }
    }
}