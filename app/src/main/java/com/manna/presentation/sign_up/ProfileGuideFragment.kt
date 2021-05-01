package com.manna.presentation.sign_up

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import com.gun0912.tedpermission.TedPermissionResult
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.common.plusAssign
import com.manna.databinding.FragmentProfileGuideBinding
import com.manna.ext.toast
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.wswon.picker.ImagePickerFragment
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
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.sign_up_profile_guide_title)
            layoutTitleBar.ivBack.setOnClickListener {
                onBackPressed()
            }
            ivImage.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_alpha)
            tvAlbum.setOnClickListener {
                showImagePicker()
            }
        }
    }

    private fun showNext(imageUri: Uri) {
        parentFragmentManager.beginTransaction().hide(this@ProfileGuideFragment).commit()
        val fragment = ProfileConfirmFragment.newInstance(imageUri)
        parentFragmentManager.beginTransaction()
            .add(R.id.fl_sign_up, fragment, fragment::class.java.simpleName).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_IMAGE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageUri =
                        data?.getParcelableExtra<Uri>(ImagePickerFragment.ARG_IMAGE_URI)
                    if (imageUri != null) {
                        showNext(imageUri)
                    }
                }
            }
        }
    }

    private fun showImagePicker() {
        checkPermissions {
            val imagePickerFragment = ImagePickerFragment()
            imagePickerFragment.setTargetFragment(this, REQ_IMAGE_PICKER)
            imagePickerFragment.show(
                parentFragmentManager,
                imagePickerFragment::class.java.simpleName
            )
        }
    }

    private fun checkPermissions(success: () -> Unit) {
        compositeDisposable += TedRx2Permission.with(requireContext())
            .setRationaleTitle("저장소 접근 권한")
            .setRationaleMessage("사진을 가져오기 위해 권한에 동의해 주세요.")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .request()
            .subscribe({ tedPermissionResult: TedPermissionResult ->
                if (tedPermissionResult.isGranted) {
                    success()
                } else {
                    toast("허용되지 않은 권한 : ${tedPermissionResult.deniedPermissions}")
                }
            }, { throwable: Throwable? ->

            })
    }


    private fun onBackPressed() {
        val fragment =
            parentFragmentManager.fragments.findLast { it is BaseFragment<*> && it !== this }
        if (fragment != null) {
            parentFragmentManager.beginTransaction().show(fragment).commit()
        }
        parentFragmentManager.beginTransaction().remove(this@ProfileGuideFragment)
            .commit()
    }

    companion object {
        private const val REQ_IMAGE_PICKER = 100

        fun newInstance() = ProfileGuideFragment()
    }
}