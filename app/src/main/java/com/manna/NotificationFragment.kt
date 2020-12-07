package com.manna

import com.manna.common.BaseFragment
import com.manna.databinding.FragmentNotificationBinding

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(R.layout.fragment_notification) {

    companion object {
        fun newInstance() =
            NotificationFragment()
    }
}
