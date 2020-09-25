package com.manna.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manna.common.BaseRecyclerViewAdapter

@BindingAdapter("bind:replaceAll")
fun RecyclerView.replaceAll(list: List<Any>?) {
    @Suppress("UNCHECKED_CAST")
    (adapter as? BaseRecyclerViewAdapter<Any, *, *>)?.replaceAll(list)
}

@BindingAdapter("bind:addAll")
fun RecyclerView.addAll(list: List<Any>?) {
    @Suppress("UNCHECKED_CAST")
    (adapter as? BaseRecyclerViewAdapter<Any, *, *>)?.addAll(list)
}




