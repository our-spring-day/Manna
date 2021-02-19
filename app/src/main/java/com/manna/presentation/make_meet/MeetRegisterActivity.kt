package com.manna.presentation.make_meet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseViewModel
import com.manna.databinding.ActivityMeetRegisterBinding
import com.manna.presentation.search.SearchActivity
import com.manna.presentation.search.SearchAddressResult
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


class MeetRegisterViewModel @ViewModelInject constructor() : BaseViewModel() {

    val date = MutableLiveData<Date>()
    val addressItem = MutableLiveData<SearchAddressResult>()
    val participantCount = MutableLiveData<Int>()
    val memo = MutableLiveData<String>()
    val penalty = MutableLiveData<Penalty>()
}

@AndroidEntryPoint
class MeetRegisterActivity :
    BaseActivity<ActivityMeetRegisterBinding>(R.layout.activity_meet_register) {

    private val viewModel by viewModels<MeetRegisterViewModel>()

    private val requestActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = activityResult.data?.getParcelableExtra<SearchAddressResult>(
                SearchActivity.ADDRESS_ITEM
            )

            if (result != null) {
                binding.locationLayout.content.text = result.addressName
                viewModel.addressItem.value = result
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        with(binding) {
            dateLayout.root.setOnClickListener {
                val prevDate = viewModel.date.value

                val fragment = DatePickerFragment.newInstance(prevDate)
                supportFragmentManager.setFragmentResultListener(
                    fragment::class.java.simpleName,
                    this@MeetRegisterActivity
                ) { _, data ->
                    val date: Date = data.getSerializable(DatePickerFragment.DATE_TIME) as? Date
                        ?: return@setFragmentResultListener

                    dateLayout.content.text =
                        SimpleDateFormat("MM.dd E요일 ・ a h시", Locale.KOREA).format(date)
                }

                supportFragmentManager.commit {
                    replace(android.R.id.content, fragment)
                }
            }

            locationLayout.root.setOnClickListener {
                requestActivity.launch(SearchActivity.getIntent(this@MeetRegisterActivity))
            }

            participantLayout.root.setOnClickListener {
                val prevCount = viewModel.participantCount.value ?: -1

                val fragment = ParticipantFragment.newInstance(prevCount)

                supportFragmentManager.setFragmentResultListener(
                    fragment::class.java.simpleName,
                    this@MeetRegisterActivity
                ) { _, data ->
                    val count = data.getInt(ParticipantFragment.PARTICIPANT_COUNT)

                    participantLayout.content.text = "${count}명 참석"
                }

                supportFragmentManager.commit {
                    replace(android.R.id.content, fragment)
                }
            }

            memoLayout.root.setOnClickListener {
                val prevMemo = viewModel.memo.value.orEmpty()

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

                dialog.show(supportFragmentManager, dialog::class.java.simpleName)
            }

            penaltyLayout.root.setOnClickListener {
                val prevPenalty = viewModel.penalty.value

                val dialog = PenaltyBottomSheetFragment.newInstance(prevPenalty)

                supportFragmentManager.setFragmentResultListener(
                    dialog::class.java.simpleName,
                    this@MeetRegisterActivity
                ) { _, data ->
                    val penalty = data.getParcelable<Penalty>(PenaltyBottomSheetFragment.PENALTY)

                    if (penalty != null) {
                        penaltyLayout.content.text = "벌칙: ${penalty.target}가 ${penalty.penalty}"
                    }
                }

                dialog.show(supportFragmentManager, dialog::class.java.simpleName)
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