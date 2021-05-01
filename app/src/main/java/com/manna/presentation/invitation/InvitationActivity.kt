package com.manna.presentation.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityInvitationBinding

class InvitationActivity : BaseActivity<ActivityInvitationBinding>(R.layout.activity_invitation) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra(KEY_NAME).orEmpty()
        binding.title.text = "${name}님의 약속"

    }

    companion object {
        private const val KEY_NAME = "NAME"
        fun getIntent(context: Context, name: String) =
            Intent(context, InvitationActivity::class.java)
                .putExtra(KEY_NAME, name)
    }
}