package com.manna

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityHomeBinding
import com.manna.presentation.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {

            val homeTabs = resources.getStringArray(R.array.main_tab)

            homePager.adapter = object : FragmentStatePagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int = homeTabs.count()

                override fun getItem(position: Int): Fragment =
                    when (position) {
                        0 -> FriendsFragment.newInstance()
                        1 -> MeetListFragment.newInstance()
                        2 -> NotificationFragment.newInstance()
                        3 -> SettingsFragment.newInstance()
                        else -> error("Invalid position")
                    }

                override fun getPageTitle(position: Int): CharSequence =
                    homeTabs[position]
            }

            bottomTab.setupWithViewPager(homePager)
        }
    }
}