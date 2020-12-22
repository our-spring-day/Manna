package com.manna.presentation.sign_up

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentServiceTermsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceTermsFragment :
    BaseFragment<FragmentServiceTermsBinding>(R.layout.fragment_service_terms) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbCheck.setOnClickListener {
            if (binding.cbCheck.isChecked) {
                binding.tvNext.isEnabled = true
                binding.tvNext.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_blue)
            } else {
                binding.tvNext.isEnabled = false
                binding.tvNext.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_gray)
            }
        }
    }

    companion object {
        fun newInstance() = ServiceTermsFragment()
    }
}