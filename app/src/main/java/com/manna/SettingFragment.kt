package com.manna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.manna.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {


    private lateinit var binding: FragmentSettingBinding

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

        val testButton = Button(context).apply {
            text = "앱 설정"
            setOnClickListener {
                startActivity(SettingActivity.getIntent(context))
            }
        }

        (binding.root as ViewGroup).addView(testButton)
    }

    companion object {
        fun newInstance() =
            SettingFragment()
    }
}