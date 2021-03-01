package com.manna.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
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
            FeedbackCategory(getString(R.string.error_report), false, viewModel.onClick),
            FeedbackCategory(getString(R.string.feedback), false, viewModel.onClick),
            FeedbackCategory(getString(R.string.inquiry), false, viewModel.onClick)
        )

        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.inquiry_feedback_send)
            layoutTitleBar.ivBack.setOnClickListener {
                finish()
            }
            tvSend.tvBottom.text = getString(R.string.send)
            tvSend.tvBottom.isEnabled = false
            rvCategory.run {
                adapter = feedbackAdapter
            }
        }

        feedbackAdapter.replaceAll(categoryList)

        viewModel.clickItem.observe(this, {
            binding.tvError.visibility = View.INVISIBLE
            categoryList.forEach { category ->
                category.click = false
            }
            it.click = true
            feedbackAdapter.replaceAll(categoryList)

            binding.tvSend.tvBottom.isEnabled = !binding.edtInquiryContent.text.isNullOrBlank()
        })

        binding.edtInquiryContent.doAfterTextChanged {
            if (!binding.edtInquiryContent.text.isNullOrBlank() && viewModel.clickItem.value?.click == true) {
                binding.tvError.visibility = View.INVISIBLE
                binding.tvSend.tvBottom.isEnabled = true
            } else if (viewModel.clickItem.value?.click != true) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvSend.tvBottom.isEnabled = false
            } else {
                binding.tvSend.tvBottom.isEnabled = false
            }
        }
    }
}