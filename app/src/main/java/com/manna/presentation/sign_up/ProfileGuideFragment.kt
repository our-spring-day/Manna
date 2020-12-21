package com.manna.presentation.sign_up

import android.os.Bundle
import android.view.View
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentProfileGuideBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileGuideFragment :
    BaseFragment<FragmentProfileGuideBinding>(R.layout.fragment_profile_guide) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance() = ProfileGuideFragment()
    }
}