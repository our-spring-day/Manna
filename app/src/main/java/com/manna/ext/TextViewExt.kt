package com.manna.ext

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.databinding.BindingAdapter
import com.manna.R


@BindingAdapter(value = ["bind:text", "bind:highlightWords"])
fun TextView.setTextHighlightWords(
    value: String,
    highlight: String,
) {
    val highlightColor: Int = R.color.keyColor
    text = buildSpannedString {
        append(value)
        val start = value.indexOf(highlight)
        if (start != -1) {
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this@setTextHighlightWords.context,
                        highlightColor
                    )
                ),
                start, start + highlight.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}