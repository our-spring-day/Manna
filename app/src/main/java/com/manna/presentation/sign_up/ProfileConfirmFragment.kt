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
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvModify.setOnClickListener {
            onBackPressed()
        }

        binding.tvNext.setOnClickListener {
            parentFragmentManager.beginTransaction().hide(this@ProfileConfirmFragment).commit()
            val fragment = ServiceTermsFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .add(R.id.fl_sign_up, fragment, fragment::class.java.simpleName).commit()
        }
    }

    private fun onBackPressed() {
        val fragment =
            parentFragmentManager.fragments.findLast {
                it !is ProfileConfirmFragment && it is BaseFragment<*>
            }
        if (fragment != null) {
            parentFragmentManager.beginTransaction().show(fragment).commit()
        }
        parentFragmentManager.beginTransaction().remove(this@ProfileConfirmFragment)
            .commit()
    }

    companion object {
        fun newInstance() = ProfileConfirmFragment()
    }
}