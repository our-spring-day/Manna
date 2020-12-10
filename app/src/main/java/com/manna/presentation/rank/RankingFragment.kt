package com.manna.presentation.rank

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentRankingBinding
import com.manna.presentation.User
import com.manna.presentation.location.MeetDetailViewModel
import com.manna.util.Logger


class RankingFragment : BaseFragment<FragmentRankingBinding>(R.layout.fragment_ranking) {

    private val rankingAdapter = RankingAdapter()

    private val viewModel by activityViewModels<MeetDetailViewModel>()

    private val roomId: String
        get() = arguments?.getString(ARG_ROOM_ID).orEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUser.adapter = rankingAdapter

        rankingAdapter.setOnClickListener(object : RankingAdapter.OnClickListener {
            override fun onClick(user: User) {

                val fragment = UrgingBottomFragment.newInstance()
                fragment.show(parentFragmentManager, fragment::class.java.simpleName)

//                viewModel.urgingUser(roomId, user.deviceToken)

//                viewModel.bottomUserItemClickEvent.value = Event(user)
            }
        })

        viewModel.userList.observe(viewLifecycleOwner, {
            Logger.d("$it")
            rankingAdapter.submitList(it)
        })
    }

    companion object {
        private const val ARG_ROOM_ID = "room_id"
        fun newInstance(roomId: String) = RankingFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ROOM_ID, roomId)
            }
        }
    }
}
