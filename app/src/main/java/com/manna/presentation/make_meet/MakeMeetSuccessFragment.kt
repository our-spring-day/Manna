package com.manna.presentation.make_meet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentMakeMeetSuccessBinding
import java.text.SimpleDateFormat
import java.util.*

class MakeMeetSuccessFragment :
    BaseFragment<FragmentMakeMeetSuccessBinding>(R.layout.fragment_make_meet_success) {

    private val viewModel by activityViewModels<MakeMeetViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        with(binding) {
            homeButton.setOnClickListener {
                activity?.finish()
            }
            inviteButton.setOnClickListener {

            }

            viewModel.date.value?.let {
                dateLayout.content.text =
                    SimpleDateFormat("MM.dd E요일 ・ a h시", Locale.KOREA).format(it)
            }

            viewModel.addressItem.value?.let { addressResult ->
                val addressName = addressResult.addressName
                    .split(" ")
                    .dropLast(1)
                    .joinToString(" ")

                locationLayout.content.text = addressName
            }
            viewModel.participantCount.value?.let { count ->
                participantLayout.content.text = "${count}명 참석"
            }
            viewModel.memo.value?.let { memo ->
                if (memo.isNotEmpty()) {
                    memoLayout.content.text = memo
                }
            } ?: run {
                memoLayout.root.alpha = 0.4f
                memoLayout.content.text = getString(R.string.memo_empty)

            }
            viewModel.penalty.value?.let { penalty ->
                penaltyLayout.content.text = "벌칙: ${penalty.target}가 ${penalty.penalty}"
            } ?: run {
                penaltyLayout.root.alpha = 0.4f
                memoLayout.content.text = getString(R.string.penalty_empty)
            }
        }
    }

    private fun setupViewModel() {
        with(viewModel) {


        }
    }

    companion object {
        fun newInstance(): MakeMeetSuccessFragment =
            MakeMeetSuccessFragment()
    }
}