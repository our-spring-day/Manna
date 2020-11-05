package com.manna.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.manna.R
import com.manna.databinding.FragmentRankingBinding

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentRankingBinding
    private val meetDetailAdapter = MeetDetailAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ranking, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUser.layoutManager = GridLayoutManager(context, 4)
        binding.rvUser.adapter = meetDetailAdapter
    }

    companion object {
        fun newInstance() = RankingFragment()
    }
}