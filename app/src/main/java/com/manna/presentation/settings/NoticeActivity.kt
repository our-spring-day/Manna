package com.manna.presentation.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseRecyclerViewAdapter
import com.manna.common.BaseRecyclerViewHolder
import com.manna.data.model.NoticeItem
import com.manna.databinding.ActivityNoticeBinding
import com.manna.databinding.ItemNoticeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BaseActivity<ActivityNoticeBinding>(R.layout.activity_notice) {
    private val viewModel by viewModels<SettingsViewModel>()

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
            listOf(
                NoticeItem("연재생일", "20.09.08", viewModel.onNoticeClick),
                NoticeItem("공지사항", "21.01.26", viewModel.onNoticeClick)
            )

        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.notice)
            layoutTitleBar.ivBack.setOnClickListener {
                finish()
            }
            rvNotice.run {
                adapter = noticeAdapter
            }
        }

        noticeAdapter.replaceAll(noticeList)

        viewModel.clickNoticeItem.observe(this, {
            startActivity(Intent(this, NoticeDetailActivity::class.java))
        })
    }
}