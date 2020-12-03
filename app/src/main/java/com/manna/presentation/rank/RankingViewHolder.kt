package com.manna.presentation.rank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manna.R
import com.manna.databinding.ItemOngoingBinding
import com.manna.presentation.User

sealed class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class OngoingViewHolder(parent: ViewGroup) : RankingViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_ongoing, parent, false)
    ) {

        private val binding = DataBindingUtil.bind<ItemOngoingBinding>(itemView)!!

        fun bind(item: User, listener: RankingAdapter.OnClickListener?) {
            itemView.run {
                binding.btnHurry.setOnClickListener {
                    listener?.onClick(item)
                }

                setRemainValue(item)
                binding.name.text = item.name
                binding.profileImage.let {
                    when (item.name) {
                        "이연재" -> Glide.with(this).load(R.drawable.test_2).into(it)
                        "원우석" -> Glide.with(this).load(R.drawable.image_3).into(it)
                        "윤상원" -> Glide.with(this).load(R.drawable.image_2).into(it)
                        "정재인" -> Glide.with(this).load(R.drawable.image_4).into(it)
                        "양종찬" -> Glide.with(this).load(R.drawable.image_6).into(it)
                        "최용권" -> Glide.with(this).load(R.drawable.image_1).into(it)
                        "김규리" -> Glide.with(this).load(R.drawable.image_5).into(it)
                        "이효근" -> Glide.with(this).load(R.drawable.image_7).into(it)
                        else -> Glide.with(this).load(R.drawable.test_1).into(it)
                    }
                }
            }
        }

        private fun setRemainValue(item: User) {
            binding.remainTime.text = item.remainTime?.let {
                "약 ${it / 60}분 남음"
            } ?: ""
        }
    }
}
