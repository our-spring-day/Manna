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
    lateinit var feedbackCategory : FeedbackCategory

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

        FeedbackCategory.values().forEach {
            if (it.category == arguments?.getString(CATEGORY)) {
                feedbackCategory = it
            }
        }

        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            edtContent.doAfterTextChanged {
                tvSend.isEnabled = edtContent.text.isNotBlank() && edtContent.text.isNotEmpty()
            }
            tvSend.setOnClickListener {
                CustomToast.toast(requireContext(), getString(feedbackCategory.message))?.show()
                requireActivity().finish()
            }
        }

        binding.tvTitle.text = getString(feedbackCategory.title)
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