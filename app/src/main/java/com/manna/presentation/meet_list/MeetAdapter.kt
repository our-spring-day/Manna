package com.manna.presentation.meet_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class MeetAdapter(
    private val onClickItem: (MeetListItem.MeetItem) -> Unit,
    private val onClickApply: () -> Unit,
    private val onClickAlert: () -> Unit
) :
    ListAdapter<MeetListItem, MeetListViewHolder>(object :
        DiffUtil.ItemCallback<MeetListItem>() {
        override fun areItemsTheSame(
            oldItem: MeetListItem,
            newItem: MeetListItem
        ): Boolean =
            when {
                oldItem is MeetListItem.Header && newItem is MeetListItem.Header -> {
                    oldItem.title == newItem.title
                }
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
            KEY_HEADER -> MeetListViewHolder.Header(parent, onClickApply, onClickAlert)
            KEY_MEET_RESPONSE -> MeetListViewHolder.Meet(parent, onClickItem)
            KEY_DATE_TITLE -> MeetListViewHolder.DateTitle(parent)
            else -> error("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: MeetListViewHolder, position: Int) {
        when (holder) {
            is MeetListViewHolder.Header -> holder.bind(currentList[position] as MeetListItem.Header)
            is MeetListViewHolder.Meet -> holder.bind(currentList[position] as MeetListItem.MeetItem)
            is MeetListViewHolder.DateTitle -> holder.bind(currentList[position] as MeetListItem.DateTitleItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MeetListItem.Header -> KEY_HEADER
            is MeetListItem.MeetItem -> KEY_MEET_RESPONSE
            is MeetListItem.DateTitleItem -> KEY_DATE_TITLE
        }
    }

    companion object {
        private const val KEY_HEADER = -1
        private const val KEY_MEET_RESPONSE = 0
        private const val KEY_DATE_TITLE = 1
    }
}

sealed interface MeetListItem {
    data class Header(
        val title: String,
        val isNewApply: Boolean,
        val isNewAlert: Boolean
    ) : MeetListItem

    data class MeetItem(
        val uuid: String,
        val meetName: String,
        val createTimestamp: Long,
        val locationJoinUserList: String,
        val chatJoinUserList: String,
    ) : MeetListItem

    data class DateTitleItem(val dateTitle: String) : MeetListItem
}