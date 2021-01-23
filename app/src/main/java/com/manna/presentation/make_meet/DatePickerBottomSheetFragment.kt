package com.manna.presentation.make_meet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.manna.R
import com.manna.databinding.FragmentDatePickerBinding

abstract class BaseBottomSheetFragment<B : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    BottomSheetDialogFragment() {

    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

}

class DatePickerBottomSheetFragment :
    BaseBottomSheetFragment<FragmentDatePickerBinding>(R.layout.fragment_date_picker) {


}