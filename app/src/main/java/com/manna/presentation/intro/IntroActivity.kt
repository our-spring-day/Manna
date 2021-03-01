package com.manna.presentation.intro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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

    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewUtil.setStatusBarTransparent(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.startButton.setOnClickListener {
            signIn()
//            startActivity(SignUpActivity.getIntent(this@IntroActivity))
//            finish()
        }

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

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)


            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            Logger.d("serverAuthCode : ${completedTask.result?.serverAuthCode}")
            Logger.d("idToken : ${completedTask.result?.idToken}")
            Logger.d("email : ${completedTask.result?.email}")
            Logger.d("displayName : ${completedTask.result?.displayName}")
            Logger.d("id : ${completedTask.result?.id}")
            Logger.d("familyName : ${completedTask.result?.familyName}")
            Logger.d("account?.name : ${completedTask.result?.account?.name}")
            Logger.d("account?.type : ${completedTask.result?.account?.type}")
            Logger.d("givenName : ${completedTask.result?.givenName}")
            Logger.d("grantedScopes?.toList() : ${completedTask.result?.grantedScopes?.toList()}")
            Logger.d("photoUrl : ${completedTask.result?.photoUrl}")
            Logger.d("isExpired : ${completedTask.result?.isExpired}")
            Logger.d("requestedScopes?.toList() : ${completedTask.result?.requestedScopes?.toList()}")
            Logger.d("result : ${completedTask.result}")

            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            Logger.d("GoogleSignInAccount ${account}")

        } catch (e: ApiException) {
            Logger.d("signInResult:failed code=" + e.statusCode)
        }
    }
    companion object {
        const val RC_SIGN_IN = 1000
    }
}