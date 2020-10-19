package com.manna

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.manna.network.model.meet.MeetResponseItem

class MeetAdapter : ListAdapter<MeetResponseItem, MeetViewHolder>(object :
    DiffUtil.ItemCallback<MeetResponseItem>() {
    override fun areItemsTheSame(oldItem: MeetResponseItem, newItem: MeetResponseItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: MeetResponseItem, newItem: MeetResponseItem): Boolean =
        oldItem.uuid == newItem.uuid
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetViewHolder {
        return MeetViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MeetViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}