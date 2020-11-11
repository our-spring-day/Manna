package com.manna.view

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val title = arrayOf("\uD83D\uDCAC", "\uD83D\uDC68\u200D\uD83D\uDD2C", "\uD83D\uDCCA")
    private val setOfFragments: MutableSet<Fragment> = mutableSetOf()

    override fun getItem(position: Int): Fragment {
        return setOfFragments.elementAt(position)
    }

    override fun getCount(): Int = setOfFragments.size

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
    }

    fun addFragment(fragment: Fragment) {
        setOfFragments.add(fragment)
        notifyDataSetChanged()
    }
}