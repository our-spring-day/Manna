package com.manna.presentation.sign_up

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentProfileGuideBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileGuideFragment :
    BaseFragment<FragmentProfileGuideBinding>(R.layout.fragment_profile_guide) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                (activity as SignUpActivity).remove(ProfileGuideFragment())
                (activity as SignUpActivity).replace(CreateNameFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {

        }

        binding.tvAlbum.setOnClickListener {
            (activity as SignUpActivity).replace(ProfileConfirmFragment())
        }
    }

    companion object {
        fun newInstance() = ProfileGuideFragment()
    }
}