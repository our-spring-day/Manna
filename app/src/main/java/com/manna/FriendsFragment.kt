package com.manna

import com.manna.common.BaseFragment
import com.manna.databinding.FragmentFriendsBinding

class FriendsFragment : BaseFragment<FragmentFriendsBinding>(R.layout.fragment_friends) {

    companion object {
        fun newInstance() =
            FriendsFragment()
    }
}
