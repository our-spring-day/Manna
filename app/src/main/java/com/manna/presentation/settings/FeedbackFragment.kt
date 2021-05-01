package com.manna.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment :
    BaseFragment<FragmentFeedbackBinding>(R.layout.fragment_feedback) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.inquiry_feedback_title)
            layoutTitleBar.ivBack.setOnClickListener {
                onBackPressed()
            }

            clError.setOnClickListener {
                addFragment(FeedbackCategory.ERROR)
            }

            clFeedback.setOnClickListener {
                addFragment(FeedbackCategory.FEEDBACK)
            }

            clInquiry.setOnClickListener {
                addFragment(FeedbackCategory.INQUIRY)
            }
        }
    }

    private fun addFragment(category: String) {
        parentFragmentManager.beginTransaction().hide(this@FeedbackFragment).commit()
        val fragment = FeedbackWriteFragment.newInstance(category)
        parentFragmentManager.beginTransaction()
            .add(R.id.fl_feedback, fragment, fragment::class.java.simpleName).commit()
    }

    private fun onBackPressed() {
        requireActivity().finish()
    }

    companion object {
        fun newInstance() =
            FeedbackFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}