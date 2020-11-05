package com.manna.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.manna.Logger
import com.manna.R
import com.manna.databinding.FragmentChatBinding
import com.manna.ext.HeightProvider
import com.manna.ext.ViewUtil
import gun0912.tedkeyboardobserver.TedKeyboardObserver


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

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

        HeightProvider(requireActivity()).init()
            .setHeightListener { height ->
                Logger.d("$height")
                keyboardHeight = height

                moveInputView()
            }

        TedKeyboardObserver(requireActivity())
            .listen { isShow ->
                Logger.d("$isShow")
                isShowingKeyBoard = isShow

                if (!isShow) {
                    (activity as MeetDetailActivity).resetBottomSheet(1f)
                }
                moveInputView()
            }
    }

    var keyboardHeight = 0
    var isShowingKeyBoard = false

    private fun moveInputView() {
        if (keyboardHeight != 0 && isShowingKeyBoard) {
            view?.let {
                viewAnim(ViewUtil.getScreenHeightPixels(activity) - keyboardHeight - ViewUtil.convertDpToPixel(requireContext(), 12f).toInt())
            }
        }
    }

    fun viewAnim(y: Int) {
        binding.chatInputView.animate()
            .y((y - binding.chatInputView.height).toFloat())
            .setDuration(0)
            .start()
    }

    companion object {
        fun newInstance() = ChatFragment()
    }
}