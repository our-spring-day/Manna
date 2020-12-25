package com.manna.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.manna.R
import com.manna.databinding.FragmentCheckInBinding
import com.manna.view.ProfileImageView
import com.wswon.picker.common.BaseFragment

class CheckInFragment : BaseFragment<FragmentCheckInBinding>(R.layout.fragment_check_in) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (0..5).forEach {

            val imageView = ProfileImageView(
                requireContext(),
                cornerRadius = 100f,
            ).apply {
                setImage("http://mimg.segye.com/content/image/2020/03/12/20200312506832.jpg")
                updatePadding(left = 10)
            }

            binding.profileImageLayout.addView(imageView)
        }
    }

    companion object {
        fun newInstance() =
            CheckInFragment()
    }
}