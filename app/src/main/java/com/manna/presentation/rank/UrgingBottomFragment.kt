package com.manna.presentation.rank

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.manna.R
import com.manna.common.Logger
import com.manna.databinding.FragmentUrgingBinding

class UrgingBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUrgingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_urging, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optimizeMessageLayout()
//        resources.getStringArray(R.array.urging_messages).forEachIndexed { index, message ->
//            val messageView = LayoutInflater.from(requireContext())
//                .inflate(R.layout.view_urging_message, binding.root as ViewGroup, false) as TextView
//            messageView.text = message
//
//            if (index < 6) {
//                binding.layout1.addView(messageView)
//            } else if (index < 11) {
//                binding.layout2.addView(messageView)
//            }
//        }
//
//        binding.scroll2.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
//
//            val maxScrollX = binding.scroll2[0].width - binding.scroll2.width
//            val targetMaxScrollX = binding.scroll1[0].width - binding.scroll1.width
//
//
//            val targetScrollX = targetMaxScrollX * (scrollX.toFloat() / maxScrollX.toFloat())
//
//            Logger.d("$maxScrollX $targetMaxScrollX $targetScrollX $scrollX")
//
//            binding.scroll1.smoothScrollTo(targetScrollX.toInt(), 0)
//        }

        binding.close.setOnClickListener {
            dismiss()
        }
    }

    private fun optimizeMessageLayout() {
//        if (ViewUtil.getScreenWidthPixels(requireContext()) > ViewUtil.convertDpToPixel(requireContext(), 640f).toInt()) {
//            binding.urgingMessageLayout.updateLayoutParams<ViewGroup.LayoutParams> {
//                width = ViewUtil.getScreenWidthPixels(requireContext())
//            }
//            binding.urgingMessageLayout.requestLayout()
//        } else {
//            binding.urgingMessageLayout.updateLayoutParams<ViewGroup.LayoutParams> {
//                width = ViewUtil.convertDpToPixel(requireContext(), 640f).toInt()
//            }
//            binding.urgingMessageLayout.requestLayout()
//        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        optimizeMessageLayout()
    }


    companion object {
        fun newInstance() =
            UrgingBottomFragment()
    }

}