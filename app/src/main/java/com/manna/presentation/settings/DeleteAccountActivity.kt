package com.manna.presentation.settings

import android.graphics.Rect
import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.viewModels
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityDeleteAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAccountActivity :
    BaseActivity<ActivityDeleteAccountBinding>(R.layout.activity_delete_account) {

    private val viewModel by viewModels<SettingsViewModel>()

    private val dialogListener = object : CustomDialogFragment.CustomDialogListener {
        override fun onDialogPositiveClick() {
            // 로그아웃
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.delete_account_reason)
            layoutTitleBar.ivBack.setOnClickListener {
                finish()
            }

            rbDeleteReason1.setOnCheckedChangeListener { buttonView, isChecked ->
                setEnabled()
            }

            rbDeleteReason2.setOnCheckedChangeListener { buttonView, isChecked ->
                setEnabled()
            }

            rbDeleteReason3.setOnCheckedChangeListener { buttonView, isChecked ->
                setEnabled()
            }

            rbDeleteReason4.setOnCheckedChangeListener { buttonView, isChecked ->
                setEnabled()
            }

            rbDeleteReason5.setOnCheckedChangeListener { buttonView, isChecked ->
                edtDeleteReason.isEnabled = isChecked
                setEnabled()
            }

            edtDeleteReason.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    svRoot.viewTreeObserver.addOnGlobalLayoutListener {
                        val r = Rect()
                        svRoot.getWindowVisibleDisplayFrame(r)
                        val screenHeight = svRoot.rootView.height
                        val keypadHeight = screenHeight - r.bottom
                        if (keypadHeight > screenHeight * 0.15) {
                            binding.svRoot.fullScroll(ScrollView.FOCUS_DOWN)
                        }
                    }
                }
            }

            tvDelete.setOnClickListener {
                val dialogFragment = CustomDialogFragment.newInstance(
                    getString(R.string.dialog_title_delete_account),
                    getString(R.string.dialog_subtitle_delete_account),
                    getString(R.string.yes),
                    getString(R.string.no)
                )
                dialogFragment.setOnClickListener(dialogListener)
                dialogFragment.show(supportFragmentManager, "")
            }
        }
    }

    private fun setEnabled() {
        binding.run {
            tvDelete.isEnabled =
                rbDeleteReason1.isChecked || rbDeleteReason2.isChecked || rbDeleteReason3.isChecked || rbDeleteReason4.isChecked || rbDeleteReason5.isChecked
        }
    }
}