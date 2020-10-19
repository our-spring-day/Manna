package com.manna

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.manna.databinding.FragmentSettingBinding
import com.manna.picker.ImagePickerFragment
import com.manna.view.WebSocketTestActivity
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.EngineIOException
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import javax.net.ssl.SSLContext
import javax.net.ssl.X509ExtendedTrustManager


class SettingFragment : Fragment() {


    private lateinit var binding: FragmentSettingBinding

    private val socket: Socket? by lazy {
        try {
            val options = IO.Options()
            options.path = "/ws"
            options.transports = arrayOf("websocket")
            val manager = Manager(URI("ws://echo.websocket.org"), options)
            manager.socket("", options)

//            IO.socket()
//            options.transports = arrayOf(WebSocket.NAME)
//            IO.socket("http://echo.websocket.org", options)
        } catch (e: URISyntaxException) {
            Logger.d("$e")
            null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d("socket is $socket")

//        socket?.run {
//            on(Socket.EVENT_CONNECT, onConnect)
//            on(Socket.EVENT_DISCONNECT, onDisconnect)
//            on(Socket.EVENT_CONNECT_ERROR, onConnectError)
//            on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
//            on(Socket.EVENT_MESSAGE, onNewMessage)
//            on(Socket.EVENT_ERROR) { args->
//                Logger.d("$args")
//                val exception = args[0] as? EngineIOException
//                Logger.d("${exception?.message} : ${exception?.cause}")
//            }
//            connect()
//        }
//


        binding.send.setOnClickListener {
            send(binding.inputText.text.toString())
        }

        val testButton = Button(context).apply {
            text = "앱 설정"
            setOnClickListener {
//                startActivity(SettingActivity.getIntent(context))
                startActivity(Intent(requireContext(), WebSocketTestActivity::class.java))
            }
        }

        (binding.root as ViewGroup).addView(testButton)

        binding.profileButton.setOnClickListener {
            showImagePicker()
        }

    }

    private fun send(message: String): Boolean {
        Logger.d(message)
//        socket?.send(message)
        socket?.emit(Socket.EVENT_MESSAGE, message)
        return true
    }

    private val onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        Logger.d("${args.toList()}")
        activity?.runOnUiThread {
            val data = args[0] as JSONObject

            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
            // add the message to view
//                addMessage(username, message)
        }
    }


    /**
     * 서버와 소켓 연결이 해제시 리스너
     */
    private val onConnect =
        Emitter.Listener { args ->
            Logger.d("${args.toList()}")
            activity?.runOnUiThread {
                Toast.makeText(context, "연결", Toast.LENGTH_SHORT).show()
            }
        }


    /**
     * 서버와 소켓 연결이 해제시 리스너
     */
    private val onDisconnect =
        Emitter.Listener { args ->
            Logger.d("${args.toList()}")
            activity?.runOnUiThread {
                Toast.makeText(context, "종료", Toast.LENGTH_SHORT).show()
            }
        }

    /**
     * 서버연결이 실패 했을 때 리스너
     */
    private val onConnectError =
        Emitter.Listener { args ->
            val exception = args[0] as? EngineIOException
            Logger.d("${exception?.message} / ${exception?.cause}")
            activity?.runOnUiThread {
                Toast.makeText(
                    context,
                    "연결 실패", Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        socket?.disconnect()
        socket?.off("new message", onNewMessage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            REQ_CAMERA_CAP -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    val file = File(
//                        context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                        currentPhotoName
//                    )
//
//                    val uri = FileProvider.getUriForFile(
//                        requireContext(),
//                        App.instance.context().packageName + ".fileprovider",
//                        file
//                    )
//
//                    imageAdapter.add(
//                        ImageItem(
//                            image = uri,
//                            removeClick = removeClickEvent,
//                            cameraPic = file
//                        )
//                    )
//                }
//            }
            REQ_IMAGE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uriList =
                        data?.getParcelableArrayExtra(ImagePickerFragment.ARG_IMAGE_URI_LIST)
                            ?.mapNotNull {
                                (it as? Uri)
//                                    ?.let { uri ->
//                                    ImageItem(
//                                        uri,
//                                        removeClickEvent
//                                    )
//                                }
                            }

                    val uri = uriList?.getOrNull(0)
                    binding.profileImage.setImageURI(uri)

//                    imageAdapter.addAll(uriList)
                }
            }
//            REQ_URL_INPUT -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    val responseUrl =
//                        data?.getStringExtra(UrlInputFragment.EXTRA_IMAGE_URL).orEmpty()
//
//                    if (responseUrl.isEmpty()) {
//                        return
//                    }
//
//                    imageAdapter.getItemList()
//                        .find { it.urlPic == responseUrl }
//                        ?.let {
//                            Toast.makeText(
//                                context,
//                                getString(R.string.toat_url_duplicate),
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            return
//                        }
//
//                    checkValidUrl(responseUrl)
//                }
//            }
        }
    }


    private fun showImagePicker() {
        val imagePickerFragment = ImagePickerFragment()
        imagePickerFragment.setTargetFragment(this, REQ_IMAGE_PICKER)
        imagePickerFragment.show(parentFragmentManager, DIALOG_TAG)
    }

    companion object {
        fun newInstance() =
            SettingFragment()

        private const val REQ_CAMERA_CAP = 98
        private const val REQ_URL_INPUT = 99
        private const val REQ_IMAGE_PICKER = 100
        private const val DIALOG_TAG = "IMAGE_PICKER"
    }
}