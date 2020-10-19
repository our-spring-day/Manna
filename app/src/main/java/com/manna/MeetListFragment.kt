package com.manna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.manna.databinding.FragmentMeetListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeetListFragment : Fragment() {

    private lateinit var binding: FragmentMeetListBinding

    private val viewModel by viewModels<MeetListViewModel>()

    private lateinit var meetAdapter: MeetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meet_list, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.meetDetail.setOnClickListener {
//            startActivity(Intent(requireContext(), MeetDetailActivity::class.java))
//        }

        binding.run {
            meetList.run {
                layoutManager = LinearLayoutManager(context)
                meetAdapter = MeetAdapter()
                adapter = meetAdapter
            }
        }

        viewModel.run {
            getMeetList("1234567")
            meetList.observe(viewLifecycleOwner) { meetList ->
                meetAdapter.submitList(meetList)
            }
        }
    }

    companion object {
        fun newInstance() =
            MeetListFragment()
    }
}
