package com.manna.view.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.manna.Event
import com.manna.Logger
import com.manna.R
import com.manna.databinding.FragmentRankingBinding
import com.manna.view.User
import com.manna.view.location.MeetDetailViewModel


class RankingFragment : Fragment() {
    private lateinit var binding: FragmentRankingBinding
    private val rankingAdapter = RankingAdapter()

    private val viewModel by activityViewModels<MeetDetailViewModel>()

    private val roomId: String
        get() = arguments?.getString(ARG_ROOM_ID).orEmpty()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ranking, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.rvUser.adapter = rankingAdapter


        rankingAdapter.setOnClickListener(object : RankingAdapter.OnClickListener {
            override fun onClick(user: User) {

                viewModel.urgingUser(roomId, user.deviceToken)

//                viewModel.bottomUserItemClickEvent.value = Event(user)
            }
        })

        activity
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
