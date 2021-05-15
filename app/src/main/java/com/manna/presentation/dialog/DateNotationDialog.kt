package com.manna.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.manna.R
import com.manna.databinding.DialogDateNotationBinding

class DateNotationDialog : DialogFragment() {

    private lateinit var binding: DialogDateNotationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_date_notation, container, false)
        return binding.root
    }

}