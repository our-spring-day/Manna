package com.manna.presentation.search

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class SearchAddressAdapter :
    PagingDataAdapter<SearchAddressItem, SearchAddressViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: SearchAddressViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAddressViewHolder {
        return SearchAddressViewHolder(parent)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchAddressItem>() {
            override fun areItemsTheSame(oldItem: SearchAddressItem, newItem: SearchAddressItem): Boolean =
                oldItem.addressName == newItem.addressName

            override fun areContentsTheSame(oldItem: SearchAddressItem, newItem: SearchAddressItem): Boolean =
                oldItem == newItem
        }
    }
}