package com.manna.presentation.sign_up

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = CreateNameFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_sign_up, fragment, fragment::class.java.simpleName).commit()
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, SignUpActivity::class.java)
    }
}