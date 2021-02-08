package com.manna.presentation.settings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseRecyclerViewAdapter
import com.manna.common.BaseRecyclerViewHolder
import com.manna.databinding.ActivityFeedbackBinding
import com.manna.databinding.ItemFeedbackCategoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_title_bar.view.*

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
        val categoryList = listOf(FeedbackCategory("오류 제보", false, viewModel.onClick),
            FeedbackCategory("피드백", false, viewModel.onClick),
            FeedbackCategory("문의", false, viewModel.onClick))

        binding.run {
            layoutTitleBar.tv_title.text = "문의 및 피드백 보내기"
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