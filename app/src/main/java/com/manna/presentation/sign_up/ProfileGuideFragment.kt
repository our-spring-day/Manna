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
                val fragment =
                    parentFragmentManager.fragments.findLast {
                        it !is ProfileGuideFragment && it is BaseFragment<*>
                    }
                if (fragment != null) {
                    parentFragmentManager.beginTransaction().show(fragment).commit()
                }
                parentFragmentManager.beginTransaction().remove(this@ProfileGuideFragment).commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {

        }

        binding.tvAlbum.setOnClickListener {
            parentFragmentManager.beginTransaction().hide(this@ProfileGuideFragment).commit()
            val fragment = ProfileConfirmFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .add(R.id.fl_sign_up, fragment, fragment::class.java.simpleName).commit()
        }
    }

    companion object {
        fun newInstance() = ProfileGuideFragment()
    }
}