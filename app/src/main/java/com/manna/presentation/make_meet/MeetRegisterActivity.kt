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
import java.text.SimpleDateFormat
import java.util.*


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
                val fragment = DatePickerFragment.newInstance()
                supportFragmentManager.setFragmentResultListener(
                    fragment::class.java.simpleName,
                    this@MeetRegisterActivity
                ) { _, data ->
                    val date: Date? = data.getSerializable(DatePickerFragment.DATE_TIME) as? Date
                    date ?: return@setFragmentResultListener

                    dateLayout.content.text =
                        SimpleDateFormat("MM.dd E요일 ・ a h시", Locale.KOREA).format(date)
                }

                supportFragmentManager.commit {
                    replace(android.R.id.content, fragment)
                }
            }
            locationLayout.root.setOnClickListener {
                startActivity(SearchActivity.getIntent(this@MeetRegisterActivity))
            }
            participantLayout.root.setOnClickListener {

            }
            memoLayout.root.setOnClickListener {
                val prevMemo = memoLayout.content.text.toString()

                val dialog = MemoBottomSheetFragment.newInstance(prevMemo)

                supportFragmentManager.setFragmentResultListener(
                    dialog::class.java.simpleName,
                    this@MeetRegisterActivity
                ) { _, data ->
                    val memo = data.getString(MemoBottomSheetFragment.MEMO).orEmpty()

                    if (memo.isNotEmpty()) {
                        memoLayout.content.text = memo
                    }
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