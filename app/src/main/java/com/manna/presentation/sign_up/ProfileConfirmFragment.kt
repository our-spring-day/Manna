package com.manna.presentation.sign_up

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentProfileConfirmBinding
import com.manna.ext.setImage
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

        val selectedImageUri = arguments?.getParcelable<Uri>(SELECTED_IMAGE)

        if (selectedImageUri != null) {
            binding.ivSelectedImage.setImage(selectedImageUri)
        }

        binding.tvModify.setOnClickListener {
            onBackPressed()
        }

        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.sign_up_profile_guide_title)
            layoutTitleBar.ivBack.setOnClickListener {
                onBackPressed()
            }
        }

        binding.tvNext.setOnClickListener {
            parentFragmentManager.beginTransaction().hide(this@ProfileConfirmFragment).commit()
            val fragment = ServiceTermsFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .add(R.id.fl_sign_up, fragment, fragment::class.java.simpleName).commit()
        }
    }

    private fun onBackPressed() {
        val fragment = parentFragmentManager.fragments.findLast { it is BaseFragment<*> && it !== this }
        if (fragment != null) {
            parentFragmentManager.beginTransaction().show(fragment).commit()
        }
        parentFragmentManager.beginTransaction().remove(this@ProfileConfirmFragment)
            .commit()
    }

    companion object {
        private const val SELECTED_IMAGE = "selected_image"

        fun newInstance(selectedImageUri: Uri) =
            ProfileConfirmFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SELECTED_IMAGE, selectedImageUri)
                }
            }
    }
}