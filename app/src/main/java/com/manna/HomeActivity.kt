package com.manna

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityHomeBinding
import com.manna.presentation.make_meet.MeetRegisterActivity
import com.manna.util.ViewUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewUtil.setStatusBarTransparent(this)

        changeTab(0)
        binding.run {
            addButton.setOnClickListener {
                startActivity(MeetRegisterActivity.getIntent(this@HomeActivity))
            }

            meetListTab.setOnClickListener {
                changeTab(0)
            }

            profileTab.setOnClickListener {
                changeTab(1)
            }
        }
    }

    private fun changeTab(position: Int) {
        if (selectedPosition == position) return

        val prevFragment = supportFragmentManager.findFragmentByTag("$selectedPosition")
        if (prevFragment != null) {
            supportFragmentManager.commitNow {
                hide(prevFragment)
            }
        }

        val fragment = supportFragmentManager.findFragmentByTag("$position")
        if (fragment != null) {
            supportFragmentManager.commit {
                show(fragment)
            }
        } else {
            supportFragmentManager.commit {
                add(R.id.frag_container, getTabFragment(position), "$position")
            }
        }

        selectedPosition = position
    }


    private fun getTabFragment(position: Int): Fragment {
        return when (position) {
            0 -> MeetListFragment.newInstance()
            1 -> ProfileFragment.newInstance()
            else -> error("Invalid position")
        }
    }
}