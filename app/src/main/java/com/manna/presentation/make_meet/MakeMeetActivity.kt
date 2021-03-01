package com.manna.presentation.make_meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityMakeMeetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeMeetActivity :
    BaseActivity<ActivityMakeMeetBinding>(R.layout.activity_make_meet) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, MakeMeetActivity::class.java)
    }
}