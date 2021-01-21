package com.manna.presentation.make_meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.hilt.lifecycle.ViewModelInject
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseViewModel
import com.manna.databinding.ActivityMeetRegisterBinding
import dagger.hilt.android.AndroidEntryPoint


class MeetRegisterViewModel @ViewModelInject constructor() : BaseViewModel() {

}

@AndroidEntryPoint
class MeetRegisterActivity :
    BaseActivity<ActivityMeetRegisterBinding>(R.layout.activity_meet_register) {

    private val viewModel by viewModels<MeetRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setupView()

    }

    private fun setupView() {

        with(binding) {



            sendButton.setOnClickListener {

            }
        }

    }

    private fun setupViewModel() {
        viewModel

    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, MeetRegisterActivity::class.java).apply {

            }
    }
}