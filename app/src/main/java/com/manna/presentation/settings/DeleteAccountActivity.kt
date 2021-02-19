package com.manna.presentation.settings

import android.os.Bundle
import androidx.activity.viewModels
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityDeleteAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAccountActivity :
    BaseActivity<ActivityDeleteAccountBinding>(R.layout.activity_delete_account) {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.run {
            layoutTitleBar.tvTitle.text = "탈퇴하려는 이유를\n알려주세요."
        }


    }
}