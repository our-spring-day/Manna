package com.manna.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.manna.ChatResponse
import com.manna.Logger
import com.manna.R
import com.manna.UserHolder
import com.manna.databinding.FragmentChatBinding
import com.manna.databinding.ItemChatBinding
import com.manna.databinding.ItemMyChatBinding
import com.manna.ext.HeightProvider
import com.manna.ext.ViewUtil
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_meet_detail.*
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter :
    ListAdapter<ChatResponse, ChatAdapterViewHolder>(
        object : DiffUtil.ItemCallback<ChatResponse>() {
            override fun areItemsTheSame(oldItem: ChatResponse, newItem: ChatResponse): Boolean =
                oldItem.message?.createTimestamp == newItem.message?.createTimestamp


            override fun areContentsTheSame(oldItem: ChatResponse, newItem: ChatResponse): Boolean =
                oldItem == newItem

        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapterViewHolder =
        when (viewType) {
            CHAT -> ChatAdapterViewHolder.ChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
            )
            MY_CHAT -> ChatAdapterViewHolder.MyChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_my_chat, parent, false)
            )
            else -> error("Invalid viewType")
        }


    override fun onBindViewHolder(holder: ChatAdapterViewHolder, position: Int) {
        when (holder) {
            is ChatAdapterViewHolder.ChatViewHolder -> holder.bind(currentList[position])
            is ChatAdapterViewHolder.MyChatViewHolder -> holder.bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].sender?.deviceToken) {
            UserHolder.userResponse?.deviceId -> MY_CHAT
            else -> CHAT
        }
    }

    companion object {
        private const val CHAT = 0
        private const val MY_CHAT = 1
    }
}


sealed class ChatAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class ChatViewHolder(view: View) : ChatAdapterViewHolder(view) {

        private val binding = DataBindingUtil.bind<ItemChatBinding>(itemView)!!

        fun bind(item: ChatResponse) {

            Logger.d("${item.type}")
            when (item.type) {
                ChatResponse.Type.CHAT -> {
                    binding.message.text = item.message?.message.orEmpty()
                    binding.name.text = item.sender?.username.orEmpty()

                    item.message?.createTimestamp?.let {
                        binding.date.text =
                            SimpleDateFormat(
                                "h:mm",
                                Locale.KOREA
                            ).format(item.message?.createTimestamp)
                    }
                }
                ChatResponse.Type.JOIN, ChatResponse.Type.LEAVE -> {

                }
            }
        }
    }

    class MyChatViewHolder(view: View) : ChatAdapterViewHolder(view) {
        private val binding = DataBindingUtil.bind<ItemMyChatBinding>(itemView)!!

        fun bind(item: ChatResponse) {
            when (item.type) {
                ChatResponse.Type.CHAT -> {
                    binding.message.text = item.message?.message.orEmpty()

                    item.message?.createTimestamp?.let {
                        binding.date.text =
                            SimpleDateFormat(
                                "h:mm",
                                Locale.KOREA
                            ).format(item.message?.createTimestamp)
                    }
                }
                ChatResponse.Type.JOIN, ChatResponse.Type.LEAVE -> {

                }
            }

        }
    }
}

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private var keyboardHeight = 0
    private var chatSocket: Socket? = null
    private lateinit var chatAdapter: ChatAdapter
    private val onChatConnectReceiver = Emitter.Listener { args ->
        activity?.runOnUiThread {
            Logger.d("${args.map { it.toString() }}")
        }
    }


    private val onChatReceiver = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val response = args.getOrNull(0)

            val chatResponse = Gson().fromJson(response.toString(), ChatResponse::class.java)
            Logger.d("chatResponse: $chatResponse")
            if (chatResponse.type == ChatResponse.Type.CHAT) {
                chatAdapter.submitList(chatAdapter.currentList + chatResponse)
            }
        }
    }

    override fun onDestroy() {
        chatSocket?.disconnect()
        chatSocket?.off(CHAT_CONNECT, onChatReceiver)
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
            val chat = binding.inputChat.text.toString()
            if (chat.isNotEmpty()) {
                chatSocket?.emit(CHAT_MESSAGE, chat)
                binding.inputChat.text.clear()
            }
        }

        binding.chatView.run {
            layoutManager = LinearLayoutManager(requireContext())
            chatAdapter = ChatAdapter()
            adapter = chatAdapter
        }

        connect()

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

    private fun connect() {
        if (chatSocket?.connected() == true) return

        val options = IO.Options()
        options.query =
            "mannaID=96f35135-390f-496c-af00-cdb3a4104550&deviceToken="

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
    }

    override fun onDestroyView() {
        super.onDestroyView()

        chatSocket?.disconnect()
        chatSocket?.off(CHAT_CONNECT, onChatConnectReceiver)
        chatSocket?.off(CHAT_MESSAGE, onChatReceiver)
    }


    fun inputViewTransY(y: Int) {
        binding.chatInputView.animate()
            .y((y - binding.chatInputView.height).toFloat())
            .setDuration(0)
            .start()

        binding.chatView.requestLayout()
    }

    companion object {
        private const val CHAT_CONNECT = "chatConnect"
        private const val CHAT_MESSAGE = "chat"

        fun newInstance() = ChatFragment()
    }
}