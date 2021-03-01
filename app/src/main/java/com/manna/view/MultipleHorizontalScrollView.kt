package com.manna.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
) : HorizontalScrollView(context, attrs, defStyle) {

    init {
        val rootView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        addView(rootView)

        setOnScrollChangeListener { _, scrollX, _, _, _ ->
            val maxScrollX = getChildAt(0).width - width

            notifyScrollX(scrollX.toFloat() / maxScrollX)
        }

        setup(rootView, resources.getStringArray(R.array.urging_messages).toList())
    }

    private fun notifyScrollX(scrollXPercent: Float) {
        (getChildAt(0) as LinearLayout).children.forEach { view ->
            val horizontalScrollView = view as HorizontalScrollView

            val maxScrollX = horizontalScrollView[0].width - horizontalScrollView.width
            val scrollX = maxScrollX * scrollXPercent

            horizontalScrollView.smoothScrollTo(scrollX.toInt(), 0)
        }
    }

    private fun setup(rootView: LinearLayout, messages: List<String>) {
        (0 until messages.size / 4).forEach { _ ->
            val horizontalScrollView = HorizontalScrollView(context).also { scrollView ->
                scrollView.overScrollMode = View.OVER_SCROLL_NEVER
                scrollView.isHorizontalScrollBarEnabled = false

                scrollView.setOnTouchListener { _, _ ->
                    true
                }
            }
            val linearLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            rootView.addView(horizontalScrollView)
            horizontalScrollView.addView(linearLayout)
        }

        messages.forEachIndexed { index, message ->
            val messageView = LayoutInflater.from(context)
                .inflate(R.layout.view_urging_message, this, false) as TextView
            messageView.text = message

            val horizontalScrollView = rootView.getChildAt(index / 4) as HorizontalScrollView
            val container = horizontalScrollView.getChildAt(0) as LinearLayout

            container.addView(messageView)
        }
    }

}