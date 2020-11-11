package com.manna

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.manna.databinding.ItemMeetBinding
import com.manna.network.model.meet.MeetResponseItem

class MeetViewHolder(parent: ViewGroup, private val onClickItem: (MeetResponseItem) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_meet, parent, false)
    ) {

    private val binding: ItemMeetBinding? by lazy {
        DataBindingUtil.bind(itemView)
    }

    fun bind(item: MeetResponseItem) {
        binding?.run {
            root.setOnClickListener {
                onClickItem.invoke(item)
            }
            this.item = item
            executePendingBindings()
        }
    }
}