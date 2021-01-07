package com.manna

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gun0912.tedpermission.TedPermissionResult
import com.manna.databinding.FragmentMeetListBinding
import com.manna.presentation.location.MeetDetailActivity
import com.manna.presentation.search.SearchActivity
import com.manna.util.UserHolder
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.wswon.picker.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MeetListFragment : BaseFragment<FragmentMeetListBinding>(R.layout.fragment_meet_list) {

    private val viewModel by viewModels<MeetListViewModel>()

    private lateinit var meetAdapter: MeetAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            meetList.run {
                layoutManager = LinearLayoutManager(context)
                meetAdapter = MeetAdapter { clickedItem ->

                    TedRx2Permission.with(requireContext())
                        .setRationaleTitle("위치정보 권한 요청")
                        .setRationaleMessage("항상 허용으로 좀 해주세요 ㅠ") // "we need permission for read contact and find your location"
                        .setPermissions(
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_FINE_LOCATION else Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                        .request()
                        .subscribe({ tedPermissionResult: TedPermissionResult ->
                            if (tedPermissionResult.isGranted) {
                                startActivity(
                                    MeetDetailActivity.getIntent(
                                        requireContext(),
                                        clickedItem.uuid.orEmpty()
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Permission Denied ${tedPermissionResult.deniedPermissions}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }, { throwable: Throwable? ->

                        })

                }
                adapter = meetAdapter
            }
        }

        viewModel.run {
            getMeetList(UserHolder.deviceId)
            meetList.observe(viewLifecycleOwner, { meetList ->
                meetAdapter.submitList(meetList)
            })
        }

        binding.route.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

    }

    companion object {
        fun newInstance() =
            MeetListFragment()
    }
}
