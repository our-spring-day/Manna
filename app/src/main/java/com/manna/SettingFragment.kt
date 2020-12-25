package com.manna

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.manna.databinding.FragmentSettingBinding
import com.wswon.picker.ImagePickerFragment
import com.wswon.picker.common.BaseFragment


class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileButton.setOnClickListener {
            showImagePicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_IMAGE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageUri =
                        data?.getParcelableExtra<Uri>(ImagePickerFragment.ARG_IMAGE_URI)
                    binding.profileImage.setImageURI(imageUri)
                }
            }
        }
    }


    private fun showImagePicker() {
        val imagePickerFragment = ImagePickerFragment()
        imagePickerFragment.setTargetFragment(this, REQ_IMAGE_PICKER)
        imagePickerFragment.show(parentFragmentManager, DIALOG_TAG)
    }

    companion object {
        fun newInstance() =
            SettingFragment()

        private const val REQ_IMAGE_PICKER = 100
        private const val DIALOG_TAG = "IMAGE_PICKER"
    }
}