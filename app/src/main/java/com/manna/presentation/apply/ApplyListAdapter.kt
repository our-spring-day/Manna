package com.manna.presentation.apply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.manna.R
import com.manna.data.model.Apply
import com.manna.data.model.ApplyItem
import com.manna.data.model.UserItem
import com.manna.databinding.ItemApplyBinding
import com.manna.databinding.ItemApplyTitleBinding

class ApplyListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var applyList = mutableListOf<Apply>()
    private var listener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    interface OnClickListener {
        fun onClick(poster: Apply)
    }

    fun addItem(itemList: List<Apply>) {
        applyList.clear()
        applyList.addAll(itemList)
        notifyItemRangeChanged(0, applyList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ViewType.TITLE -> {
                val viewDataBinding = DataBindingUtil.inflate<ItemApplyTitleBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_apply_title, parent, false
                )
                return TitleViewHolder(viewDataBinding)
            }
            else -> {
                val viewDataBinding = DataBindingUtil.inflate<ItemApplyBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_apply, parent, false
                )
                return ApplyViewHolder(viewDataBinding)
            }
        }
    }

    override fun getItemCount() = applyList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TitleViewHolder -> holder.bind(applyList[position], listener)
            is ApplyViewHolder -> holder.bind(applyList[position], listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (applyList[position]) {
            is ApplyItem -> ViewType.TITLE
            is UserItem -> ViewType.APPLY
        }
    }

    class TitleViewHolder(private val binding: ItemApplyTitleBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: Apply, listener: OnClickListener?) {
            val apply = item as? ApplyItem
            binding.tvTitle.text = apply?.location + " · " + apply?.date + " · " + apply?.dDay
        }
    }

    class ApplyViewHolder(private val binding: ItemApplyBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: Apply, listener: OnClickListener?) {
            val user = item as? UserItem
            binding.user = user
        }
    }
}

object ViewType {
    const val TITLE = 0
    const val APPLY = 1
}
