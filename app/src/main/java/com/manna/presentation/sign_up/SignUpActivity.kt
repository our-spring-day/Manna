package com.manna.presentation.sign_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replace(CreateNameFragment())
    }

    fun replace(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragmentTag =
            supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName)
        if (fragmentTag != null) {
            transaction.show(fragment).commit()
        } else {
            transaction.replace(R.id.fl_sign_up, fragment, fragment::class.java.simpleName)
                .commit()
        }
    }

    fun remove(fragment: Fragment) {
        val fragmentTag =
            supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName)
                ?: return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(fragmentTag).commit()
    }
}