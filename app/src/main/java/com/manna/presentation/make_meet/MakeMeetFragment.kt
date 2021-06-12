package com.manna.presentation.make_meet

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.common.EventObserver
import com.manna.databinding.FragmentMakeMeetBinding
import com.manna.presentation.search.SearchAddressActivity
import com.manna.presentation.search.SearchAddressResult
import java.text.SimpleDateFormat
import java.util.*

class MakeMeetFragment : BaseFragment<FragmentMakeMeetBinding>(R.layout.fragment_make_meet) {

    private val viewModel by activityViewModels<MakeMeetViewModel>()

    private val requestSearchAddressActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = activityResult.data?.getParcelableExtra<SearchAddressResult>(
                SearchAddressActivity.ADDRESS_ITEM
            )

            if (result != null) {
                val addressName = result.addressName
                    .split(" ")
                    .dropLast(1)
                    .joinToString(" ")

                binding.locationLayout.content.text = addressName
                viewModel.addressItem.value = result
            }

            checkState()
        }
    }

    private val clickDate: (View) -> Unit = {
        val prevDate = viewModel.date.value

        val fragment = DatePickerFragment.newInstance(prevDate)
        parentFragmentManager.setFragmentResultListener(
            fragment::class.java.simpleName,
            this@MakeMeetFragment
        ) { _, data ->
            val date: Date = data.getSerializable(DatePickerFragment.DATE_TIME) as? Date
                ?: return@setFragmentResultListener

            binding.dateLayout.content.text =
                SimpleDateFormat("MM.dd E요일 ・ a h시", Locale.KOREA).format(date)

            viewModel.date.value = date
            checkState()
        }

        parentFragmentManager.commit {
            hide(this@MakeMeetFragment)
            add(R.id.container, fragment)
        }
    }

    private val clickLocation: (View) -> Unit = {
        requestSearchAddressActivity.launch(SearchAddressActivity.getIntent(requireContext()))
    }

    private val clickParticipant: (View) -> Unit = {
        val prevCount = viewModel.participantCount.value ?: -1

        val fragment = ParticipantFragment.newInstance(prevCount)

        parentFragmentManager.setFragmentResultListener(
            fragment::class.java.simpleName,
            this@MakeMeetFragment
        ) { _, data ->
            val count = data.getInt(ParticipantFragment.PARTICIPANT_COUNT)

            binding.participantLayout.content.text = "${count}명 참석"
            viewModel.participantCount.value = count
        }

        parentFragmentManager.commit {
            hide(this@MakeMeetFragment)
            add(R.id.container, fragment)
        }
    }

    private val clickMemo: (View) -> Unit = {
        val prevMemo = viewModel.memo.value.orEmpty()

        val dialog = MemoBottomSheetFragment.newInstance(prevMemo)

        parentFragmentManager.setFragmentResultListener(
            dialog::class.java.simpleName,
            this@MakeMeetFragment
        ) { _, data ->
            val memo = data.getString(MemoBottomSheetFragment.MEMO).orEmpty()

            if (memo.isNotEmpty()) {
                binding.memoLayout.content.text = memo
                viewModel.memo.value = memo
            }
        }

        dialog.show(parentFragmentManager, dialog::class.java.simpleName)
    }

    private val clickPenalty: (View) -> Unit = {
        val prevPenalty = viewModel.penalty.value

        val dialog = PenaltyBottomSheetFragment.newInstance(prevPenalty)

        parentFragmentManager.setFragmentResultListener(
            dialog::class.java.simpleName,
            this@MakeMeetFragment
        ) { _, data ->
            val penalty = data.getParcelable<Penalty>(PenaltyBottomSheetFragment.PENALTY)

            if (penalty != null) {
                binding.penaltyLayout.content.text = "벌칙: ${penalty.target}가 ${penalty.penalty}"
                viewModel.penalty.value = penalty
            }
        }

        dialog.show(parentFragmentManager, dialog::class.java.simpleName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun checkState() {
        with(binding) {
            val hasRequired =
                dateLayout.content.text.isNotEmpty() && locationLayout.content.text.isNotEmpty()

            sendButton.isEnabled = hasRequired
            sendButton.setText(if (hasRequired) R.string.make_meet_button_enable else R.string.make_meet_button_disable)
        }

    }

    private fun setupView() {
        with(binding) {
            dateLayout.root.setOnClickListener(clickDate)

            locationLayout.root.setOnClickListener(clickLocation)

            participantLayout.root.setOnClickListener(clickParticipant)

            memoLayout.root.setOnClickListener(clickMemo)

            penaltyLayout.root.setOnClickListener(clickPenalty)

            sendButton.setOnClickListener {
                viewModel.make()
            }

            close.setOnClickListener {
                activity?.finish()
            }
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            success.observe(viewLifecycleOwner, EventObserver {
                parentFragmentManager.commit {
                    replace(R.id.container, MakeMeetSuccessFragment.newInstance())
                }
            })
        }

    }

    companion object {
        fun newInstance(): MakeMeetFragment =
            MakeMeetFragment()
    }
}