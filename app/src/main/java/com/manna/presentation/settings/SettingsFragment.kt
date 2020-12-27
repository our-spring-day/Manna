package com.manna.presentation.settings

import android.os.Bundle
import android.view.View
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}