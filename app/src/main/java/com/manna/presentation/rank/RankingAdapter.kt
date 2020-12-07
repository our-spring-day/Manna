package com.manna.presentation.rank

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import com.manna.presentation.User

class RankingAdapter :
    ListAdapter<User, RankingViewHolder>(
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.deviceToken == newItem.deviceToken

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    ) {
    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(user: User)
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder =
        RankingViewHolder.OngoingViewHolder(parent)

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        (holder as RankingViewHolder.OngoingViewHolder).bind(currentList[position], onClickListener)
    }

}
