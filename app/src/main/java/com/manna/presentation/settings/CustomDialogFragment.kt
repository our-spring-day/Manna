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
    lateinit var listener: CustomDialogListener

    interface CustomDialogListener {
        fun onDialogPositiveClick()
    }

    fun setOnClickListener(listener: CustomDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_custom, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitle.text = arguments?.getString(TITLE)
            tvSubtitle.text = arguments?.getString(SUBTITLE)
            tvConfirm.text = arguments?.getString(POSITIVE)
            tvCancel.text = arguments?.getString(NEGATIVE)
        }

        if (binding.tvSubtitle.text.isNotEmpty()) {
            binding.tvSubtitle.visibility = View.VISIBLE
        }

        val tenDp = ViewUtil.convertDpToPixel(requireContext(), 10f)

        binding.clRoot.background = GradientDrawable().apply {
            cornerRadius = tenDp
            setColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            listener.onDialogPositiveClick()
            dismiss()
        }
    }

    companion object {
        private const val TITLE = "title"
        private const val SUBTITLE = "subtitle"
        private const val POSITIVE = "positive"
        private const val NEGATIVE = "negative"

        fun newInstance(title: String, subtitle: String? = "", positive: String, negative: String) =
            CustomDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(SUBTITLE, subtitle)
                    putString(POSITIVE, positive)
                    putString(NEGATIVE, negative)
                }
            }
    }
}
