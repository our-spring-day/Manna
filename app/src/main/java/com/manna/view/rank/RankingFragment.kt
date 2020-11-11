package com.manna.view.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.manna.Event
import com.manna.Logger
import com.manna.R
import com.manna.databinding.FragmentRankingBinding
import com.manna.view.User
import com.manna.view.location.MeetDetailAdapter
import com.manna.view.location.MeetDetailViewModel

class RankingFragment : Fragment() {
    private lateinit var binding: FragmentRankingBinding
    private val meetDetailAdapter = MeetDetailAdapter()

    private val viewModel by activityViewModels<MeetDetailViewModel>()

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

        binding.rvUser.layoutManager = GridLayoutManager(context, 4)
        binding.rvUser.adapter = meetDetailAdapter


        meetDetailAdapter.setOnClickListener(object : MeetDetailAdapter.OnClickListener {
            override fun onClick(user: User) {
                viewModel.bottomUserItemClickEvent.value = Event(user)
            }
        })

        viewModel.userList.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Logger.d("$it")
            meetDetailAdapter.submitList(it)
        })


    }

    companion object {
        fun newInstance() = RankingFragment()
    }
}
