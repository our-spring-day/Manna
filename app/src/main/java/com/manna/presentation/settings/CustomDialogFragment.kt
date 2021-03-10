package com.manna.presentation.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.manna.R
import com.manna.databinding.DialogCustomBinding
import com.manna.util.ViewUtil


class CustomDialogFragment : DialogFragment() {

    private lateinit var binding: DialogCustomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_custom, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tenDp = ViewUtil.convertDpToPixel(requireContext(), 10f)
        val oneDp = ViewUtil.convertDpToPixel(requireContext(), 1f).toInt()

        binding.clRoot.background = GradientDrawable().apply {
            val array = floatArrayOf(tenDp, tenDp, tenDp, tenDp, tenDp, tenDp, tenDp, tenDp)
            cornerRadii = array
            setColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        binding.tvCancel.background = GradientDrawable().apply {
            val array = floatArrayOf(tenDp, tenDp, tenDp, tenDp, tenDp, tenDp, tenDp, tenDp)
            cornerRadii = array
            setStroke(oneDp, ContextCompat.getColor(requireContext(), R.color.keyColor))
        }

        binding.tvConfirm.background = GradientDrawable().apply {
            val array = floatArrayOf(tenDp, tenDp, tenDp, tenDp, tenDp, tenDp, tenDp, tenDp)
            cornerRadii = array
            setColor(ContextCompat.getColor(requireContext(), R.color.keyColor))
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            dismiss()
        }
    }
}