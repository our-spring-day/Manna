package com.manna.presentation.make_meet

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentParticipantBinding
import com.manna.ext.openKeyboard
import com.manna.ext.toast

class ParticipantFragment :
    BaseFragment<FragmentParticipantBinding>(R.layout.fragment_participant) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        with(binding) {
            val count = arguments?.getInt(PARTICIPANT_COUNT) ?: 0
            if (count > 0) {
                inputCount.setText("$count")
            }

            inputCount.openKeyboard()

            submitButton.setOnClickListener {
                val participantCount = inputCount.text.toString().toIntOrNull() ?: 0

                if (participantCount < 1) {
                    toast(R.string.empty_participant_count)
                    return@setOnClickListener
                }

                val data = Bundle().apply { putInt(PARTICIPANT_COUNT, participantCount) }
                setFragmentResult(this@ParticipantFragment::class.java.simpleName, data)
                finish()
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun finish() {
        val prevFragment = parentFragmentManager.fragments.find { it is BaseFragment<*> && it !== this }

        parentFragmentManager.commit {
            if (prevFragment!= null) {
                show(prevFragment)
            }
            remove(this@ParticipantFragment)
        }
    }

    companion object {
        const val PARTICIPANT_COUNT = "participant_count"

        fun newInstance(count: Int) =
            ParticipantFragment().apply {
                arguments = Bundle().apply {
                    putInt(PARTICIPANT_COUNT, count)
                }
            }
    }
}


