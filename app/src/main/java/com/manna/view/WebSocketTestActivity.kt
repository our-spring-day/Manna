package com.manna.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.manna.Logger
import com.manna.R
import com.manna.network.api.MeetApi
import com.manna.network.model.meet.MeetResponseItem
import io.crossbar.autobahn.websocket.WebSocketConnection
import io.crossbar.autobahn.websocket.exceptions.WebSocketException
import io.crossbar.autobahn.websocket.interfaces.IWebSocketConnectionHandler
import io.crossbar.autobahn.websocket.types.ConnectionResponse

class WebSocketTestActivity : AppCompatActivity() {

    private val mConnection: WebSocketConnection = WebSocketConnection()
    private lateinit var inputView: EditText
    private lateinit var outputView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_websocket)

        val meetItem = intent.getParcelableExtra<MeetResponseItem>(MEET_ITEM)

        Toast.makeText(this, meetItem?.toString(), Toast.LENGTH_LONG).show()

        inputView = findViewById(R.id.input)
        outputView = findViewById(R.id.output_window)
        findViewById<Button>(R.id.send_button).setOnClickListener {

            val jsonObject = JsonObject().apply {
                addProperty("sender", "우석")
                addProperty("message", "우석메시지")
            }

            val jsonString = JsonObject().apply {
                add("chatToServer", jsonObject)
            }
            Logger.d("$jsonString")

            mConnection.sendMessage(jsonString.toString())
        }
        start()
    }

    private fun start() {

        val wsuri = "ws://ec2-3-34-179-87.ap-northeast-2.compute.amazonaws.com:9999/chat/chatToServer"//""ws://ec2-13-124-151-24.ap-northeast-2.compute.amazonaws.com:9999/manna"
        try {
            mConnection.connect(wsuri, object : IWebSocketConnectionHandler {

                override fun onMessage(payload: ByteArray?, isBinary: Boolean) {
                    Logger.d("$payload $isBinary")
                }

                override fun onConnect(response: ConnectionResponse?) {
                    Logger.d("$response")
                }

                override fun onPing() {
                    Logger.d("")
                }

                override fun onPing(payload: ByteArray?) {
                    Logger.d("$payload")
                }

                override fun onPong() {
                    Logger.d("")
                }

                override fun onPong(payload: ByteArray?) {
                    Logger.d("$payload")
                }

                override fun setConnection(connection: WebSocketConnection?) {
                    Logger.d("$connection")
                }

                @SuppressLint("SetTextI18n")
                override fun onOpen() {
                    Logger.d("Status: Connected to $wsuri")
                    outputView.text = outputView.text.toString() + "\nconnected to " + wsuri
                }

                override fun onMessage(payload: String?) {
                    Logger.d("Got echo: $payload")
                    outputView.text = outputView.text.toString() + "\nGot echo: " + payload
                }

                override fun onClose(code: Int, reason: String?) {
                    Logger.d("Connection lost.")
                    outputView.text = outputView.text.toString() + "\nConnection lost: " + reason
                }
            })
        } catch (e: WebSocketException) {
            Logger.d(e.toString())
        }
    }

    companion object {
        private const val TAG = "WebSocketTestActivity"

        private const val MEET_ITEM = "meet_item"
        fun getIntent(context: Context, meetItem: MeetResponseItem) =
            Intent(context, WebSocketTestActivity::class.java).apply {
                putExtra(MEET_ITEM, meetItem)
            }
    }
}