package com.manna.presentation.make_meet

import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentDatePickerBinding
import com.manna.databinding.FragmentMemoBinding

class DatePickerBottomSheetFragment :
    BaseFragment<FragmentDatePickerBinding>(R.layout.fragment_date_picker) {

    companion object {
        fun newInstance() =
            DatePickerBottomSheetFragment()
    }
}

class MemoBottomSheetFragment :
    BaseFragment<FragmentMemoBinding>(R.layout.fragment_memo) {

    companion object {
        fun newInstance() =
            MemoBottomSheetFragment()
    }
}