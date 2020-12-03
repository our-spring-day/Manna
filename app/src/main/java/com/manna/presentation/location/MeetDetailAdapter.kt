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
import com.manna.Logger
import com.manna.R
import com.manna.presentation.User
import kotlinx.android.synthetic.main.item_round_view.view.*

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

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_round_view, parent, false)
    ) {

        private val remainValue = itemView.findViewById<TextView>(R.id.remain_value)

        fun bind(item: User, listener: OnClickListener?) {
            itemView.run {
                setOnClickListener {
                    listener?.onClick(item)
                }

                setRemainValue(item)
                if (itemViewType == VIEW_TYPE_TEXT) {
                    tv_name.visibility = View.VISIBLE
                    iv_image.visibility = View.GONE
                    if (item.name.orEmpty().length > 2) {
                        tv_name.text = item.name?.subSequence(1, item.name.length)
                    } else {
                        tv_name.text = item.name
                    }
                } else {
                    tv_name.visibility = View.GONE
                    iv_image.visibility = View.VISIBLE
                    when (item.name) {
                        "이연재" -> Glide.with(this).load(R.drawable.test_2).into(iv_image)
                        "원우석" -> Glide.with(this).load(R.drawable.image_3).into(iv_image)
                        "윤상원" -> Glide.with(this).load(R.drawable.image_2).into(iv_image)
                        "정재인" -> Glide.with(this).load(R.drawable.image_4).into(iv_image)
                        "양종찬" -> Glide.with(this).load(R.drawable.image_6).into(iv_image)
                        "최용권" -> Glide.with(this).load(R.drawable.image_1).into(iv_image)
                        "김규리" -> Glide.with(this).load(R.drawable.image_5).into(iv_image)
                        "이효근" -> Glide.with(this).load(R.drawable.image_7).into(iv_image)
                        else -> Glide.with(this).load(R.drawable.test_1).into(iv_image)
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