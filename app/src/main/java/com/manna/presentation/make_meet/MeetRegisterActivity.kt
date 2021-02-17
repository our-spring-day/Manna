package com.manna.presentation.make_meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.hilt.lifecycle.ViewModelInject
import com.manna.R
import com.manna.databinding.ActivityMeetRegisterBinding
import com.manna.presentation.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint


class MeetRegisterViewModel @ViewModelInject constructor() : com.manna.common.BaseViewModel() {

}

@AndroidEntryPoint
class MeetRegisterActivity :
    com.manna.common.BaseActivity<ActivityMeetRegisterBinding>(R.layout.activity_meet_register) {

    private val viewModel by viewModels<MeetRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setupView()
        setupViewModel()

    }

    private fun setupView() {

        with(binding) {
            dateLayout.root.setOnClickListener {
//                DatePickerBottomSheetFragment.newInstance().show(supportFragmentManager, "")
            }
            locationLayout.root.setOnClickListener {
                startActivity(SearchActivity.getIntent(this@MeetRegisterActivity))
            }
            participantLayout.root.setOnClickListener {

            }
            memoLayout.root.setOnClickListener {
//                MemoBottomSheetFragment.newInstance().show(supportFragmentManager, "")
            }
            penaltyLayout.root.setOnClickListener {

            }
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