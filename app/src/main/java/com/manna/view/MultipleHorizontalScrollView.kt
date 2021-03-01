package com.manna.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.get
import com.manna.R

@RequiresApi(Build.VERSION_CODES.M)
class MultipleHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : LinearLayout(context, attrs, defStyle) {

    init {
        orientation = VERTICAL
        setup(resources.getStringArray(R.array.urging_messages).toList())
    }

    private var currentTouchView: View? = null

    private fun notifyScrollX(scrollXPercent: Float) {
        children.forEach { view ->
            if (currentTouchView === view) return@forEach
            val horizontalScrollView = view as HorizontalScrollView

            val maxScrollX = horizontalScrollView[0].width - horizontalScrollView.width
            val scrollX = maxScrollX * scrollXPercent

            horizontalScrollView.smoothScrollTo(scrollX.toInt(), 0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setup(messages: List<String>) {
        (0 until messages.size / 4).forEach { _ ->
            val horizontalScrollView = HorizontalScrollView(context).also { scrollView ->
                scrollView.overScrollMode = View.OVER_SCROLL_NEVER
                scrollView.isHorizontalScrollBarEnabled = false

                scrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
                    val maxScrollX = scrollView[0].width - scrollView.width

                    if (currentTouchView === scrollView) {
                        notifyScrollX(scrollX.toFloat() / maxScrollX)
                    }
                }

                scrollView.setOnTouchListener { view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN,
                        MotionEvent.ACTION_MOVE -> {
                            currentTouchView = view
                        }
                        MotionEvent.ACTION_CANCEL,
                        MotionEvent.ACTION_UP -> {

                        }
                    }
                    false
                }
            }
            val linearLayout = LinearLayout(context).apply {
                orientation = HORIZONTAL
            }

            addView(horizontalScrollView)
            horizontalScrollView.addView(linearLayout)
        }

        messages.forEachIndexed { index, message ->
            val messageView = LayoutInflater.from(context)
                .inflate(R.layout.view_urging_message, this, false) as TextView
            messageView.text = message

            val horizontalScrollView = getChildAt(index / 4) as HorizontalScrollView
            val container = horizontalScrollView.getChildAt(0) as LinearLayout

            container.addView(messageView)
        }
    }

}