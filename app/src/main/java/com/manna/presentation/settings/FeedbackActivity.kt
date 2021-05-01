package com.manna.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackActivity : BaseActivity<ActivityFeedbackBinding>(R.layout.activity_feedback) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = FeedbackFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_feedback, fragment, fragment::class.java.simpleName).commit()
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, FeedbackActivity::class.java)
    }
}