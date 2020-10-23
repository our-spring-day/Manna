package com.manna.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manna.R

class MeetDetailAdapter :
    RecyclerView.Adapter<MeetDetailAdapter.ViewHolder>() {
//    private val items = mutableListOf<User>()
    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
//        fun onClick(user: User)
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun getItemCount(): Int = 10
//        items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
//    =
//        holder.bind(items[position], onClickListener)

//    fun addData(user: User) {
//        items.clear()
//        items.add(user)
//        notifyDataSetChanged()
//    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_round_view, parent, false)
    ) {
//        fun bind(item: User, listener: OnClickListener?) {
//            itemView.run {
//                setOnClickListener {
//                    listener?.onClick(item)
//                }
//                tv_name.text = item.name
//            }
//        }
    }
}