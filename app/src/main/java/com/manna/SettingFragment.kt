package com.manna

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.manna.databinding.FragmentSettingBinding
import com.manna.picker.ImagePickerFragment
import com.manna.presentation.SocketIOTestActivity


class SettingFragment : Fragment() {


    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testButton = Button(context).apply {
            text = "앱 설정"
            setOnClickListener {
//                startActivity(SettingActivity.getIntent(context))
                startActivity(Intent(requireContext(), SocketIOTestActivity::class.java))
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
//            REQ_CAMERA_CAP -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    val file = File(
//                        context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                        currentPhotoName
//                    )
//
//                    val uri = FileProvider.getUriForFile(
//                        requireContext(),
//                        App.instance.context().packageName + ".fileprovider",
//                        file
//                    )
//
//                    imageAdapter.add(
//                        ImageItem(
//                            image = uri,
//                            removeClick = removeClickEvent,
//                            cameraPic = file
//                        )
//                    )
//                }
//            }
            REQ_IMAGE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uriList =
                        data?.getParcelableArrayExtra(ImagePickerFragment.ARG_IMAGE_URI_LIST)
                            ?.mapNotNull {
                                (it as? Uri)
//                                    ?.let { uri ->
//                                    ImageItem(
//                                        uri,
//                                        removeClickEvent
//                                    )
//                                }
                            }

                    val uri = uriList?.getOrNull(0)
                    binding.profileImage.setImageURI(uri)

//                    imageAdapter.addAll(uriList)
                }
            }
//            REQ_URL_INPUT -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    val responseUrl =
//                        data?.getStringExtra(UrlInputFragment.EXTRA_IMAGE_URL).orEmpty()
//
//                    if (responseUrl.isEmpty()) {
//                        return
//                    }
//
//                    imageAdapter.getItemList()
//                        .find { it.urlPic == responseUrl }
//                        ?.let {
//                            Toast.makeText(
//                                context,
//                                getString(R.string.toat_url_duplicate),
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            return
//                        }
//
//                    checkValidUrl(responseUrl)
//                }
//            }
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

        private const val REQ_CAMERA_CAP = 98
        private const val REQ_URL_INPUT = 99
        private const val REQ_IMAGE_PICKER = 100
        private const val DIALOG_TAG = "IMAGE_PICKER"
    }
}