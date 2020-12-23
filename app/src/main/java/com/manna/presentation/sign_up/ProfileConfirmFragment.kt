package com.manna.presentation.sign_up

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentProfileConfirmBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileConfirmFragment :
    BaseFragment<FragmentProfileConfirmBinding>(R.layout.fragment_profile_confirm) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                (activity as SignUpActivity).remove(ProfileConfirmFragment())
                (activity as SignUpActivity).replace(ProfileGuideFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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