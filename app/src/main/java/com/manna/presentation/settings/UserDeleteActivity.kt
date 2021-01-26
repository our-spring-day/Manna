package com.manna.presentation.settings

import android.os.Bundle
import androidx.databinding.library.baseAdapters.BR
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseRecyclerViewAdapter
import com.manna.common.BaseRecyclerViewHolder
import com.manna.data.model.NoticeItem
import com.manna.databinding.ActivityNoticeBinding
import com.manna.databinding.ItemNoticeBinding
import com.manna.ext.replaceAll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_title_bar.view.*

@AndroidEntryPoint
class UserDeleteActivity : BaseActivity<ActivityNoticeBinding>(R.layout.activity_notice) {
    private val noticeAdapter by lazy {
        object :
            BaseRecyclerViewAdapter<NoticeItem, ItemNoticeBinding, BaseRecyclerViewHolder<ItemNoticeBinding>>(
                R.layout.item_notice,
                variableId = BR.item
            ) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noticeList =
            arrayListOf(NoticeItem("연재생일", "20.09.08"), NoticeItem("공지사항", "21.01.26"))

        binding.run {
            layoutTitleBar.tv_title.text = "공지사항"
            rvNotice.run {
                adapter = noticeAdapter
                replaceAll(noticeList)
            }
        }
    }
}