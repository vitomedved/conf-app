package com.example.confapp

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.View

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                Log.d("TABLAYOUT", "Clicked on 0")
                Fragment()
            }
            //1 -> BlankFragment()
            else -> {
                Log.d("TABLAYOUT", "Clicked on 1")
                ScheduleFragment()
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