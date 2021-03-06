package com.manna.presentation.settings

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityServiceTermsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceTermsActivity :
    BaseActivity<ActivityServiceTermsBinding>(R.layout.activity_service_terms) {
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.run {
            tvContent.movementMethod = ScrollingMovementMethod()
            layoutTitleBar.tvTitle.text = getString(R.string.service_terms)
            layoutTitleBar.ivBack.setOnClickListener {
                finish()
            }
        }
    }
}