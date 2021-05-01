package com.manna.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentFeedbackWriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackWriteFragment :
    BaseFragment<FragmentFeedbackWriteBinding>(R.layout.fragment_feedback_write) {

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

        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            edtContent.doAfterTextChanged {
                tvSend.isEnabled = edtContent.text.isNotBlank() && edtContent.text.isNotEmpty()
            }
            tvSend.setOnClickListener {
                when (arguments?.getString(CATEGORY)) {
                    FeedbackCategory.ERROR -> CustomToast.toast(
                        requireContext(),
                        getString(R.string.toast_error_report)
                    )?.show()
                    FeedbackCategory.FEEDBACK -> CustomToast.toast(
                        requireContext(),
                        getString(R.string.toast_feedback)
                    )?.show()
                    FeedbackCategory.INQUIRY -> CustomToast.toast(
                        requireContext(),
                        getString(R.string.toast_inquiry)
                    )?.show()
                }
                requireActivity().finish()
            }
        }

        when (arguments?.getString(CATEGORY)) {
            FeedbackCategory.ERROR -> binding.tvTitle.text = getString(R.string.error_report)
            FeedbackCategory.FEEDBACK -> binding.tvTitle.text = getString(R.string.feedback)
            FeedbackCategory.INQUIRY -> binding.tvTitle.text = getString(R.string.inquiry)
        }
    }

    private fun onBackPressed() {
        val fragment =
            parentFragmentManager.fragments.findLast {
                it !is FeedbackWriteFragment && it is BaseFragment<*>
            }
        if (fragment != null) {
            parentFragmentManager.beginTransaction().show(fragment).commit()
        }
        parentFragmentManager.beginTransaction().remove(this@FeedbackWriteFragment)
            .commit()
    }

    companion object {
        private const val CATEGORY = "category"

        fun newInstance(category: String) =
            FeedbackWriteFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY, category)
                }
            }
    }
}