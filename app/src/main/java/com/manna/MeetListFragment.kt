package com.manna

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.manna.databinding.FragmentMeetListBinding
import com.manna.view.MeetDetailActivity

class MeetListFragment : Fragment() {


    private lateinit var binding: FragmentMeetListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meet_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.meetDetail.setOnClickListener {
            startActivity(Intent(requireContext(), MeetDetailActivity::class.java))
        }
    }

    companion object {
        fun newInstance() =
            MeetListFragment()
    }
}
