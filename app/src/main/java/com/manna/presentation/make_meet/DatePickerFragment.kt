package com.manna.presentation.make_meet

import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentDatePickerBinding


class DatePickerFragment :
    BaseFragment<FragmentDatePickerBinding>(R.layout.fragment_date_picker) {

    companion object {
        fun newInstance() =
            DatePickerFragment()
    }
}