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
            layoutTitleBar.tvTitle.text = getString(R.string.delete_account_reason)
            layoutTitleBar.ivBack.setOnClickListener {
                finish()
            }

            rgDeleteReason.setOnCheckedChangeListener { group, checkedId ->
                edtDeleteReason.isEnabled = checkedId == R.id.rb_delete_reason_5
                tvDelete.isEnabled = true
            }
        }
    }
}