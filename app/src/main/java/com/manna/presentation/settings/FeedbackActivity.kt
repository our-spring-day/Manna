package com.manna.presentation.settings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import com.manna.R
import com.manna.common.BaseActivity

import com.manna.databinding.ActivityFeedbackBinding
import com.manna.databinding.ItemFeedbackCategoryBinding
import com.wswon.picker.adapter.BaseRecyclerViewAdapter
import com.wswon.picker.adapter.BaseRecyclerViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackActivity : BaseActivity<ActivityFeedbackBinding>(R.layout.activity_feedback) {
    private val viewModel by viewModels<SettingsViewModel>()

    private val feedbackAdapter by lazy {
        object :
            BaseRecyclerViewAdapter<FeedbackCategory, ItemFeedbackCategoryBinding, BaseRecyclerViewHolder<ItemFeedbackCategoryBinding>>(
                R.layout.item_feedback_category,
                variableId = BR.item
            ) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryList = listOf(
            FeedbackCategory("오류 제보", false, viewModel.onClick),
            FeedbackCategory("피드백", false, viewModel.onClick),
            FeedbackCategory("문의", false, viewModel.onClick)
        )

        binding.run {
            layoutTitleBar.tvTitle.text = "문의 및 피드백 보내기"
            rvCategory.run {
                adapter = feedbackAdapter
            }
        }

        feedbackAdapter.replaceAll(categoryList)

        viewModel.clickItem.observe(this, {
            categoryList.forEach { category ->
                category.click = false
            }
            it.click = true
            Toast.makeText(this, it.category, Toast.LENGTH_SHORT).show()
            feedbackAdapter.replaceAll(categoryList)
        })
    }
}