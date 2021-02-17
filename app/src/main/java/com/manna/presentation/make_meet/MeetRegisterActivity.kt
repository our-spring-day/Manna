package com.manna.presentation.make_meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.hilt.lifecycle.ViewModelInject
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseViewModel
import com.manna.databinding.ActivityMeetRegisterBinding
import com.manna.presentation.search.SearchActivity
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
        setupViewModel()
    }

    private fun setupView() {

        with(binding) {
            dateLayout.root.setOnClickListener {
                supportFragmentManager.commit {
                    replace(android.R.id.content, DatePickerFragment.newInstance())
                }
            }
            locationLayout.root.setOnClickListener {
                startActivity(SearchActivity.getIntent(this@MeetRegisterActivity))
            }
            participantLayout.root.setOnClickListener {

            }
            memoLayout.root.setOnClickListener {
                val dialog = MemoBottomSheetFragment.newInstance("")
                supportFragmentManager.setFragmentResultListener(
                    dialog::class.java.simpleName,
                    this@MeetRegisterActivity
                ) { requestKey, data ->

                }

                dialog.show(supportFragmentManager, "")
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