package com.manna.presentation.make_meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityMeetRegisterBinding

class MeetRegisterActivity :
    BaseActivity<ActivityMeetRegisterBinding>(R.layout.activity_meet_register) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, MeetRegisterActivity::class.java).apply {

            }
    }
}