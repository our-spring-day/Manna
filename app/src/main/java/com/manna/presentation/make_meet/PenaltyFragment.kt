package com.manna.presentation.make_meet

import android.os.Bundle
import android.view.View
import com.manna.R
import com.manna.common.BaseBottomSheetFragment
import com.manna.databinding.FragmentPenaltyBinding

class PenaltyFragment :
    BaseBottomSheetFragment<FragmentPenaltyBinding>(R.layout.fragment_penalty) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance() =
            PenaltyFragment()
    }
}