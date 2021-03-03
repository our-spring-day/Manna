package com.manna.presentation.settings

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.manna.R
import com.manna.databinding.ViewToastBinding

object CustomToast {
    fun toast(context: Context, message: String): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ViewToastBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_toast, null, false)

        binding.tvMessage.text = message

        return Toast(context).apply {
            setGravity(Gravity.CENTER, 0, 0)
            duration = Toast.LENGTH_LONG
            view = binding.root
        }
    }
}