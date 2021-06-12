package com.manna.presentation.meet_list

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.gun0912.tedpermission.TedPermissionResult
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.common.plusAssign
import com.manna.databinding.FragmentMeetListBinding
import com.manna.ext.toast
import com.manna.presentation.location.MeetDetailActivity
import com.manna.util.SpacingDecoration
import com.manna.util.ToolbarHelper
import com.manna.util.UserHolder
import com.manna.util.ViewUtil
import com.tedpark.tedpermission.rx2.TedRx2Permission
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MeetListFragment : BaseFragment<FragmentMeetListBinding>(R.layout.fragment_meet_list) {

    private val viewModel by viewModels<MeetListViewModel>()

    private val meetAdapter: MeetAdapter by lazy {
        MeetAdapter(onClickMeetItem, onClickApply, onClickAlert)
    }

    private val onClickMeetItem: (MeetListItem.MeetItem) -> Unit = { clickedItem ->
        showMeetDetail(clickedItem)
    }

    private val onClickApply: () -> Unit = {
        activity?.invalidateOptionsMenu()
    }

    private val onClickAlert: () -> Unit = {

    }

    private var toolbarHelper: ToolbarHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initToolbarHelper()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.updatePadding(top = ViewUtil.getStatusBarHeight(requireContext()))
        activity?.invalidateOptionsMenu()
        setupView()
        setupViewModel()
    }

    private fun initToolbarHelper() {
        val appBarLayout =
            (binding.root as ViewGroup).children.find { it is AppBarLayout } as? AppBarLayout

        if (appBarLayout != null) {
            toolbarHelper =
                ToolbarHelper(requireActivity() as AppCompatActivity, appBarLayout).apply {
                    setToolbarTitle("약속 목록")
                }
        }
    }

    private fun setupView() {
        with(binding) {
            meetList.run {
                layoutManager = LinearLayoutManager(context)

                addItemDecoration(getDecoration())
                adapter = meetAdapter
            }
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            getMeetList(UserHolder.deviceId)
            meetList.observe(viewLifecycleOwner, { meetList ->
                meetAdapter.submitList(meetList)
            })
        }
    }

    private fun getDecoration(): RecyclerView.ItemDecoration =
        SpacingDecoration(bottom = ViewUtil.convertDpToPixel(requireContext(), 16f).toInt())

    private fun showMeetDetail(clickedItem: MeetListItem.MeetItem) {
        checkPermissions {
            startActivity(
                MeetDetailActivity.getIntent(
                    requireContext(),
                    clickedItem.uuid
                )
            )
        }
    }

    private fun checkPermissions(success: () -> Unit) {
        compositeDisposable += TedRx2Permission.with(requireContext())
            .setRationaleTitle("위치정보 권한 요청")
            .setRationaleMessage("항상 허용으로 해주세요")
            .setPermissions(
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_FINE_LOCATION else Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            .request()
            .subscribe({ tedPermissionResult: TedPermissionResult ->
                if (tedPermissionResult.isGranted) {
                    success()
                } else {
                    toast("허용되지 않은 권한 ${tedPermissionResult.deniedPermissions}")
                }
            }, { throwable: Throwable? ->

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_meet_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.apply -> {
                onClickApply()
            }

            R.id.alert -> {
                onClickAlert()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        fun newInstance() =
            MeetListFragment()
    }
}



