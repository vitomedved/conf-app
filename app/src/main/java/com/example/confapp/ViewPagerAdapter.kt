package com.example.confapp

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ScheduleFragment()
            }
            //1 -> BlankFragment()
            else -> {
                return ScheduleFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Activity feed"
            //1 -> "Second Tab"
            else -> {
                return "Schedule"
            }
        }
    }
}