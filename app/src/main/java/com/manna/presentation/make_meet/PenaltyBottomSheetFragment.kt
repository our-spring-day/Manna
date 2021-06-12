package com.manna.presentation.make_meet

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manna.R
import com.manna.common.BaseBottomSheetFragment
import com.manna.databinding.FragmentPenaltyBinding
import com.manna.ext.toast
import kotlinx.parcelize.Parcelize

@Parcelize
data class Penalty(
    val target: String,
    val penalty: String
) : Parcelable

class PenaltyBottomSheetFragment :
    BaseBottomSheetFragment<FragmentPenaltyBinding>(R.layout.fragment_penalty) {

    private var target = ""
    private var penalty = ""

    private val targetCheckerList by lazy {
        listOf(
            binding.targetLayout1.checker,
            binding.targetLayout2.checker
        )
    }

    private val penaltyCheckerList by lazy {
        listOf(
            binding.penaltyLayout1.checker,
            binding.penaltyLayout2.checker,
            binding.penaltyLayout3.checker,
            binding.penaltyLayout4.checker
        )
    }

    override fun onStart() {
        super.onStart()
        view?.run {
            post {
                val parent = parent as View
                val params = parent.layoutParams as CoordinatorLayout.LayoutParams
                val behavior = params.behavior
                val bottomSheetBehavior = behavior as BottomSheetBehavior

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        val prevPenalty = arguments?.getParcelable<Penalty>(PENALTY)
        if (prevPenalty != null) {
            target = prevPenalty.target
            this.penalty = prevPenalty.target
        }

        with(binding) {
            onClickTargetLayout(targetLayout1.root)
            onClickTargetLayout(targetLayout2.root)

            onClickPenaltyLayout(penaltyLayout1.root)
            onClickPenaltyLayout(penaltyLayout2.root)
            onClickPenaltyLayout(penaltyLayout3.root)
            onClickPenaltyLayout(penaltyLayout4.root)

            submitButton.setOnClickListener {
                if (target.isEmpty()) {
                    toast("대상을 선택해 주세요.")
                    return@setOnClickListener
                } else if (penalty.isEmpty()) {
                    toast("벌칙을 선택해 주세요.")
                    return@setOnClickListener
                }

                val data = Bundle().apply {
                    putParcelable(PENALTY, Penalty(target, penalty))
                }
                setFragmentResult(this@PenaltyBottomSheetFragment::class.java.simpleName, data)
                dismiss()
            }
        }
    }

    private fun onClickTargetLayout(layout: View) {
        layout.setOnClickListener {
            targetCheckerList.forEach {
                if (it.isVisible) it.isGone = true
            }

            val checker = layout.findViewById<ImageView>(R.id.checker)
            checker.isVisible = !checker.isVisible
            setTarget(layout.findViewById<TextView>(R.id.content).hint.toString())
            checkSubmitState()
        }
    }

    private fun onClickPenaltyLayout(layout: View) {
        layout.setOnClickListener {
            penaltyCheckerList.forEach {
                if (it.isVisible) it.isGone = true
            }

            val checker = layout.findViewById<ImageView>(R.id.checker)
            checker.isVisible = !checker.isVisible
            setPenalty(layout.findViewById<TextView>(R.id.content).hint.toString())
            checkSubmitState()
        }
    }

    private fun checkSubmitState() {
        val isEnable = target.isNotEmpty() && penalty.isNotEmpty()
        binding.submitButton.isEnabled = isEnable
        binding.submitButton.setText(if (isEnable) R.string.penalty_button_enable else R.string.penalty_button_disable)
    }

    private fun setTarget(target: String) {
        this.target = target
    }

    private fun setPenalty(penalty: String) {
        this.penalty = penalty
    }

    companion object {
        const val PENALTY = "penalty"

        fun newInstance(penalty: Penalty? = null) =
            PenaltyBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    if (penalty != null) {
                        putParcelable(PENALTY, penalty)
                    }
                }
            }
    }
}