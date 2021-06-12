package com.manna.presentation.meet_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.manna.R
import com.manna.databinding.ItemMeetBinding
import com.manna.databinding.ItemMeetDateTitleBinding
import com.manna.databinding.ItemMeetHeaderBinding
import java.text.SimpleDateFormat
import java.util.*


sealed class MeetListViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    class Header(
        parent: ViewGroup,
        private val binding: ItemMeetHeaderBinding =
            ItemMeetHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : MeetListViewHolder(binding.root) {

        fun bind(item: MeetListItem.Header) {
            binding.title.text = item.title
            binding.btnApply.setImageResource(if (item.isNewApply) R.drawable.ic_mail_new else R.drawable.ic_mail)
            binding.btnAlert.setImageResource(if (item.isNewAlert) R.drawable.ic_bell_new else R.drawable.ic_bell)
        }
    }

    class Meet(
        parent: ViewGroup,
        private val onClickItem: (MeetListItem.MeetItem) -> Unit,
        private val binding: ItemMeetBinding =
            ItemMeetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : MeetListViewHolder(binding.root) {

        fun bind(item: MeetListItem.MeetItem) {
            binding.run {
                mapButton.setOnClickListener {
                    onClickItem.invoke(item)
                }
                startButton.setOnClickListener {

                }

                address.text = item.meetName

                time.text = SimpleDateFormat("M시").format(Date(item.createTimestamp))

                participantContainer.post {
                    (0..4).forEach {
                        val imageView = ImageView(itemView.context)
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            participantContainer.height,
                            participantContainer.height
                        )

                        Glide.with(itemView.context)
                            .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load("http://mimg.segye.com/content/image/2020/03/12/20200312506832.jpg")
                            .into(imageView)

                        participantContainer.addView(imageView)
                    }
                }

                executePendingBindings()
            }
        }
    }

    class DateTitle(
        parent: ViewGroup,
        private val binding: ItemMeetDateTitleBinding =
            ItemMeetDateTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : MeetListViewHolder(binding.root) {

        fun bind(item: MeetListItem.DateTitleItem) {
            binding.tvTitle.text = item.dateTitle
        }
    }
}


