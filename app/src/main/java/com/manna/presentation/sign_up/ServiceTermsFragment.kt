package com.manna.presentation.sign_up

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentServiceTermsBinding
import com.manna.presentation.HomeActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ServiceTermsFragment :
    BaseFragment<FragmentServiceTermsBinding>(R.layout.fragment_service_terms) {

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

        binding.cbCheck.setOnClickListener {
            if (binding.cbCheck.isChecked) {
                binding.tvSignUp.isEnabled = true
                binding.tvSignUp.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_blue)
            } else {
                binding.tvSignUp.isEnabled = false
                binding.tvSignUp.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_gray)
            }
        }

        binding.tvCheck.setOnClickListener {
            if (binding.cbCheck.isChecked) {
                binding.cbCheck.isChecked = false
                binding.tvSignUp.isEnabled = false
                binding.tvSignUp.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_gray)
            } else {
                binding.cbCheck.isChecked = true
                binding.tvSignUp.isEnabled = true
                binding.tvSignUp.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_blue)
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(HomeActivity.getIntent(requireContext(), isWelcome = true))
            requireActivity().finish()
        }

        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.sign_up_service_terms_title)
            layoutTitleBar.ivBack.setOnClickListener {
                onBackPressed()
            }
            tvContent.movementMethod = ScrollingMovementMethod()
        }
    }

    private fun onBackPressed() {
        val fragment =
            parentFragmentManager.fragments.findLast { it is BaseFragment<*> && it !== this }
        if (fragment != null) {
            parentFragmentManager.beginTransaction().show(fragment).commit()
        }
        parentFragmentManager.beginTransaction().remove(this@ServiceTermsFragment).commit()
    }

    companion object {
        fun newInstance() = ServiceTermsFragment()
    }
}