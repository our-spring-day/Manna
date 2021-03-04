package com.manna.presentation.intro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.manna.DeviceUtil
import com.manna.HomeActivity
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.EventObserver
import com.manna.common.Logger
import com.manna.databinding.ActivityIntroBinding
import com.manna.presentation.sign_up.SignUpActivity
import com.manna.util.ViewUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>(R.layout.activity_intro) {

    private val viewModel by viewModels<IntroViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewUtil.setStatusBarTransparent(this)

        binding.startButton.setOnClickListener {
            startActivity(SignUpActivity.getIntent(this@IntroActivity))
            finish()
        }
        handleDeepLink()

        binding.homeButton.setOnClickListener {
            startActivity(HomeActivity.getIntent(this@IntroActivity))
            finish()
        }

        return

        // viewModel.checkDevice(DeviceUtil.getAndroidID(this))

        viewModel.isValidDevice.observe(this, EventObserver { isValid ->
            if (isValid) {
                startActivity(HomeActivity.getIntent(this@IntroActivity))
                finish()
            } else {
                val message = if (binding.registerNameGroup.isVisible) {
                    "기기인증이 실패했어요. 다시 시도해보세요."
                } else {
                    "처음 오셨군요? 사용할 닉네임을 입력해주세요"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                binding.registerNameGroup.isVisible = true
            }
        })

        binding.submitName.setOnClickListener {
            val name = binding.inputName.text.toString()
            if (name.isNotEmpty()) {
                viewModel.registerDevice(name, DeviceUtil.getAndroidID(this))
            }
        }
    }


    private fun handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                if (pendingDynamicLinkData == null) {
                    Logger.d("No have dynamic link")
                    return@addOnSuccessListener
                }
                val deepLink = pendingDynamicLinkData.link
                Logger.d("deepLink: $deepLink");

                val segment = deepLink?.lastPathSegment
                Logger.d("segment $segment")
                when (segment) {
//                        SEGMENT_PROMOTION:
//                        String code = deepLink.getQueryParameter(KEY_CODE);
//                        showPromotionDialog(code);
//                        break;
                }
            }
            .addOnFailureListener { exception ->
                Logger.d("$exception")
            }

    }
}