package com.manna.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manna.Logger
import com.manna.R
import com.manna.databinding.FragmentChatBinding
import com.manna.ext.HeightProvider
import com.manna.ext.ViewUtil
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_meet_detail.*
import java.net.URI


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private var keyboardHeight = 0
    private lateinit var chatSocket: Socket

    private val onChatConnectReceiver = Emitter.Listener { args ->
        activity?.runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }


    private val onChatReceiver = Emitter.Listener { args ->
        activity?.runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }

    override fun onDestroy() {
        chatSocket.disconnect()
        chatSocket.off(CHAT_CONNECT, onChatReceiver)
        super.onDestroy()
    }


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

        binding.ddaBong.setOnClickListener {
            chatSocket.emit(CHAT_MESSAGE, binding.inputChat.text.toString())
        }


        val behavior = BottomSheetBehavior.from(requireActivity().bottom_sheet)

        val options = IO.Options()
        options.query = "mannaID=96f35135-390f-496c-af00-cdb3a4104550&deviceToken=f606564d8371e455"

        val chatManager = Manager(URI("https://manna.duckdns.org:19999"), options)
        chatSocket =
            chatManager.socket("/chat").apply {
                on(CHAT_CONNECT, onChatConnectReceiver)
                on(CHAT_MESSAGE, onChatReceiver)

                on(Socket.EVENT_CONNECT) {
                    Logger.d("EVENT_CONNECT ${it.map { it.toString() }}")
                }

                on(Socket.EVENT_DISCONNECT) {
                    Logger.d("EVENT_DISCONNECT ${it.map { it.toString() }}")
                }

                on(Socket.EVENT_MESSAGE) {
                    Logger.d("EVENT_MESSAGE ${it.map { it.toString() }}")
                }

                connect()
            }

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
        private const val CHAT_CONNECT = "chatConnect"
        private const val CHAT_MESSAGE = "chat"

        fun newInstance() = ChatFragment()
    }
}