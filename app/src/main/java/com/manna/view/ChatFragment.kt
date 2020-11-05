package com.manna.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manna.R
import com.manna.databinding.FragmentChatBinding
import com.manna.ext.HeightProvider
import com.manna.ext.ViewUtil
import kotlinx.android.synthetic.main.activity_meet_detail.*


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private var keyboardHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val behavior = BottomSheetBehavior.from(requireActivity().bottom_sheet)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (keyboardHeight > 0) {
                            inputViewTransY(
                                ViewUtil.getScreenHeightPixels(activity) - keyboardHeight - ViewUtil.convertDpToPixel(
                                    requireContext(),
                                    12f
                                ).toInt()
                            )
                        } else {
                            (activity as MeetDetailActivity).resetBottomSheet(1f)
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.inputChat.clearFocus()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        binding.inputChat.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        HeightProvider(requireActivity()).init()
            .setHeightListener { height ->
                keyboardHeight = height

                if (height > 0) {
                    inputViewTransY(
                        ViewUtil.getScreenHeightPixels(activity) - height - ViewUtil.convertDpToPixel(
                            requireContext(),
                            12f
                        ).toInt()
                    )
                } else {
                    (activity as MeetDetailActivity).resetBottomSheet(1f)
                    binding.inputChat.clearFocus()
                }
            }
    }


    fun inputViewTransY(y: Int) {
        binding.chatInputView.animate()
            .y((y - binding.chatInputView.height).toFloat())
            .setDuration(0)
            .start()
    }

    companion object {
        fun newInstance() = ChatFragment()
    }
}