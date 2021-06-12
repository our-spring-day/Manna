package com.manna.presentation.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.manna.R
import com.manna.data.model.AlertItem
import com.manna.databinding.ItemAlertBinding

class AlertListAdapter : RecyclerView.Adapter<AlertListAdapter.AlertViewHolder>() {
    var alertList = mutableListOf<AlertItem>()

    private var listener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    interface OnClickListener {
        fun onClick(poster: AlertItem)
    }

    fun addItem(itemList: List<AlertItem>) {
        alertList.clear()
        alertList.addAll(itemList)
        notifyItemRangeChanged(0, alertList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val viewDataBinding = DataBindingUtil.inflate<ItemAlertBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_alert, parent, false
        )
        return AlertViewHolder(viewDataBinding)
    }

    override fun getItemCount() = alertList.size

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alertList[position], listener)
    }

    class AlertViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: AlertItem, listener: OnClickListener?) {
            binding.apply {
                alert = item
            }
        }
    }
}
