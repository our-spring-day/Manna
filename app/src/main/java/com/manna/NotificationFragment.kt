package com.manna

import android.os.Bundle
import android.view.View
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentNotificationBinding

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(R.layout.fragment_notification) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() =
            NotificationFragment()
    }
}
