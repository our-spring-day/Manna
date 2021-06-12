package com.manna.presentation.meet_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class MeetAdapter(
    private val onClickItem: (MeetListItem.MeetItem) -> Unit
) :
    ListAdapter<MeetListItem, MeetListViewHolder>(object :
        DiffUtil.ItemCallback<MeetListItem>() {
        override fun areItemsTheSame(
            oldItem: MeetListItem,
            newItem: MeetListItem
        ): Boolean =
            when {
                oldItem is MeetListItem.MeetItem && newItem is MeetListItem.MeetItem -> {
                    oldItem.uuid == newItem.uuid
                }
                oldItem is MeetListItem.DateTitleItem && newItem is MeetListItem.DateTitleItem -> {
                    oldItem.dateTitle == newItem.dateTitle
                }
                else -> false
            }

        override fun areContentsTheSame(
            oldItem: MeetListItem,
            newItem: MeetListItem
        ): Boolean =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetListViewHolder {
        return when (viewType) {
            KEY_MEET_RESPONSE -> MeetListViewHolder.Meet(parent, onClickItem)
            KEY_DATE_TITLE -> MeetListViewHolder.DateTitle(parent)
            else -> error("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: MeetListViewHolder, position: Int) {
        when (holder) {
            is MeetListViewHolder.Meet -> holder.bind(currentList[position] as MeetListItem.MeetItem)
            is MeetListViewHolder.DateTitle -> holder.bind(currentList[position] as MeetListItem.DateTitleItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MeetListItem.MeetItem -> KEY_MEET_RESPONSE
            is MeetListItem.DateTitleItem -> KEY_DATE_TITLE
        }
    }

    companion object {
        private const val KEY_MEET_RESPONSE = 0
        private const val KEY_DATE_TITLE = 1
    }
}

sealed class MeetListItem {
    data class MeetItem(
        val uuid: String,
        val meetName: String,
        val createTimestamp: Long,
        val locationJoinUserList: String,
        val chatJoinUserList: String,
    ) : MeetListItem()

    data class DateTitleItem(val dateTitle: String) : MeetListItem()
}