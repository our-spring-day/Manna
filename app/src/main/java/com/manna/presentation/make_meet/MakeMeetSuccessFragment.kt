package com.manna.presentation.make_meet

import android.os.Bundle
import android.view.View
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentMakeMeetSuccessBinding

class MakeMeetSuccessFragment :
    BaseFragment<FragmentMakeMeetSuccessBinding>(R.layout.fragment_make_meet_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): MakeMeetSuccessFragment =
            MakeMeetSuccessFragment()
    }
}