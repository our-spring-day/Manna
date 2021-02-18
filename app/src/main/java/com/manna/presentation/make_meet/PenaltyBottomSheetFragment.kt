package com.manna.presentation.make_meet

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manna.R
import com.manna.common.BaseBottomSheetFragment
import com.manna.databinding.FragmentPenaltyBinding

class PenaltyBottomSheetFragment :
    BaseBottomSheetFragment<FragmentPenaltyBinding>(R.layout.fragment_penalty) {

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

    }

    companion object {
        fun newInstance() =
            PenaltyBottomSheetFragment()
    }
}