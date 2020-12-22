package com.manna.presentation.sign_up

import android.os.Bundle
import android.view.View
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentProfileConfirmBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileConfirmFragment :
    BaseFragment<FragmentProfileConfirmBinding>(R.layout.fragment_profile_confirm) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNext.setOnClickListener {
            (activity as SignUpActivity).replace(ServiceTermsFragment())
        }
    }

    companion object {
        fun newInstance() = ProfileConfirmFragment()
    }
}