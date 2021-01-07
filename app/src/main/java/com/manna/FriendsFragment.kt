package com.manna

import com.manna.databinding.FragmentFriendsBinding
import com.wswon.picker.common.BaseFragment

class FriendsFragment : BaseFragment<FragmentFriendsBinding>(R.layout.fragment_friends) {

    companion object {
        fun newInstance() =
            FriendsFragment()
    }
}
