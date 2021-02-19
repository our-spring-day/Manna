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
                val fragment =
                    parentFragmentManager.fragments.findLast {
                        it !is ServiceTermsFragment && it is BaseFragment<*>
                    }
                if (fragment != null) {
                    parentFragmentManager.beginTransaction().show(fragment).commit()
                }
                parentFragmentManager.beginTransaction().remove(this@ServiceTermsFragment).commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.movementMethod = ScrollingMovementMethod()
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
        binding.tvSignUp.setOnClickListener {
            requireActivity().finish()
        }
    }

    companion object {
        fun newInstance() = ServiceTermsFragment()
    }
}