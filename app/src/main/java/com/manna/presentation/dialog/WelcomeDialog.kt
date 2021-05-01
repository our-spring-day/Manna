package com.manna.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.manna.R
import com.manna.databinding.DialogWelcomeBinding
import com.manna.util.ViewUtil

class WelcomeDialog : DialogFragment() {

    private lateinit var binding: DialogWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_welcome, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tenDp = ViewUtil.convertDpToPixel(requireContext(), 10f)

        binding.panel1.background = GradientDrawable().apply {
            val array = floatArrayOf(tenDp, tenDp, tenDp, tenDp, 0f, 0f, 0f, 0f)
            cornerRadii = array
            setColor(Color.WHITE)
        }

        binding.panel2.background = GradientDrawable().apply {
            val array = floatArrayOf(0f, 0f, 0f, 0f, tenDp, tenDp, tenDp, tenDp)
            cornerRadii = array
            setColor(ContextCompat.getColor(requireContext(), R.color.keyColor))
        }
        binding.panel2.setOnClickListener {
            dismiss()
        }
    }
}