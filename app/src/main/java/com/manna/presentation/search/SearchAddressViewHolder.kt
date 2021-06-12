package com.manna.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manna.databinding.ItemSearchAddressBinding

class SearchAddressViewHolder(
    parent: ViewGroup,
    private val binding: ItemSearchAddressBinding =
        ItemSearchAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(item: SearchAddressItem) {
        binding.item = item
        binding.executePendingBindings()
    }
}