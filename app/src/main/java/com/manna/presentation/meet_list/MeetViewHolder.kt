package com.manna.presentation.meet_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.manna.R
import com.manna.databinding.ItemMeetBinding
import com.manna.network.model.meet.MeetResponseItem
import java.text.SimpleDateFormat
import java.util.*

class MeetViewHolder(parent: ViewGroup, private val onClickItem: (MeetResponseItem) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_meet, parent, false)
    ) {

    private val binding: ItemMeetBinding? by lazy {
        DataBindingUtil.bind(itemView)
    }

    fun bind(item: MeetResponseItem) {
        binding?.run {
            mapButton.setOnClickListener {
                onClickItem.invoke(item)
            }
            startButton.setOnClickListener {

            }

            address.text = item.mannaName

            if (item.createTimestamp != null) {
                time.text = SimpleDateFormat("M시").format(Date(item.createTimestamp))
            }


            participantContainer.post {
                (0..4).forEach {
                    val imageView = ImageView(itemView.context)
                    imageView.layoutParams = LinearLayout.LayoutParams(participantContainer.height, participantContainer.height)


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