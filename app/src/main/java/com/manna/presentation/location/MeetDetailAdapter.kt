package com.manna.presentation.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manna.R
import com.manna.common.Logger
import com.manna.databinding.ItemRoundViewBinding
import com.manna.presentation.User

class MeetDetailAdapter :
    ListAdapter<User, MeetDetailAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.deviceToken == newItem.deviceToken

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                false
        }
    ) {
    private var onClickListener: OnClickListener? = null
    private var itemViewType = VIEW_TYPE_TEXT

    interface OnClickListener {
        fun onClick(user: User)
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], onClickListener)
    }

    fun setItemViewType() {
        itemViewType = if (itemViewType == VIEW_TYPE_TEXT) {
            VIEW_TYPE_IMAGE
        } else {
            VIEW_TYPE_TEXT
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewType
    }

    class ViewHolder(
        parent: ViewGroup,
        private val binding: ItemRoundViewBinding = ItemRoundViewBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        private val remainValue = itemView.findViewById<TextView>(R.id.remain_value)

        fun bind(item: User, listener: OnClickListener?) {
            itemView.setOnClickListener {
                listener?.onClick(item)
            }

            setRemainValue(item)

            with(binding) {
                if (itemViewType == VIEW_TYPE_TEXT) {
                    tvName.visibility = View.VISIBLE
                    ivImage.visibility = View.GONE
                    if (item.name.length > 2) {
                        tvName.text = item.name.subSequence(1, item.name.length)
                    } else {
                        tvName.text = item.name
                    }
                } else {
                    tvName.visibility = View.GONE
                    ivImage.visibility = View.VISIBLE

                    val context = itemView.context
                    when (item.name) {
                        "이연재" -> Glide.with(context).load(R.drawable.test_2).into(ivImage)
                        "원우석" -> Glide.with(context).load(R.drawable.image_3).into(ivImage)
                        "윤상원" -> Glide.with(context).load(R.drawable.image_2).into(ivImage)
                        "정재인" -> Glide.with(context).load(R.drawable.image_4).into(ivImage)
                        "양종찬" -> Glide.with(context).load(R.drawable.image_6).into(ivImage)
                        "최용권" -> Glide.with(context).load(R.drawable.image_1).into(ivImage)
                        "김규리" -> Glide.with(context).load(R.drawable.image_5).into(ivImage)
                        "이효근" -> Glide.with(context).load(R.drawable.image_7).into(ivImage)
                        else -> Glide.with(context).load(R.drawable.test_1).into(ivImage)
                    }
                }
            }

        }

        private fun setRemainValue(item: User) {
            remainValue.isVisible = item.remainDistance != null || item.remainTime != null
            Logger.d("Ran ${remainValue.isVisible}")
            if (itemViewType == VIEW_TYPE_TEXT) {
                if (item.remainDistance != null) {
                    remainValue.text = String.format("%.1fkm", item.remainDistance)
                }
            } else {
                if (item.remainTime != null) {
                    remainValue.text = item.remainTime.toString()
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_TEXT = 0
        const val VIEW_TYPE_IMAGE = 1
    }
}