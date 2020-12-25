package com.manna

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.manna.databinding.FragmentSettingBinding
import com.wswon.picker.common.BaseFragment


class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testButton = Button(context).apply {
            text = "앱 설정"
            setOnClickListener {
                startActivity(SettingActivity.getIntent(context))
//                startActivity(Intent(requireContext(), SocketIOTestActivity::class.java))
            }
        }

        (binding.root as ViewGroup).addView(testButton)

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
                        data?.getParcelableExtra<Uri>(com.wswon.picker.ImagePickerFragment.ARG_IMAGE_URI)
                    binding.profileImage.setImageURI(imageUri)
                }
            }
        }
    }


    private fun showImagePicker() {
        val imagePickerFragment = com.wswon.picker.ImagePickerFragment()
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