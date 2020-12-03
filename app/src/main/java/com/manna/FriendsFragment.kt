package com.manna

import android.os.Bundle
import android.view.View
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentFriendsBinding

class FriendsFragment : BaseFragment<FragmentFriendsBinding>(R.layout.fragment_friends) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() =
            FriendsFragment()
    }
}
