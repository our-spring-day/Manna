package com.manna

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.manna.databinding.ActivitySettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.run {
            back.setOnClickListener {
                finish()
            }

            dateNotationPanel.setOnClickListener {
                val dialog = DateNotationDialog()
                dialog.show(supportFragmentManager, "")
            }
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, SettingActivity::class.java)
    }
}