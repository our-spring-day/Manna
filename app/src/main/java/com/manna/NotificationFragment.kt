package com.manna

import com.manna.databinding.FragmentNotificationBinding
import com.wswon.picker.common.BaseFragment

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(R.layout.fragment_notification) {

    companion object {
        fun newInstance() =
            NotificationFragment()
    }
}
