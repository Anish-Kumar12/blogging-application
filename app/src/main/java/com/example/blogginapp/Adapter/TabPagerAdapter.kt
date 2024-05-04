package com.example.blogginapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blogginapp.DescriptionFragment
import com.example.blogginapp.ReminderFragment
import com.example.blogginapp.SharedFragment


class TabPagerAdapter(activity: FragmentActivity?) : FragmentStateAdapter(activity!!) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DescriptionFragment()
            1 -> ReminderFragment()
            2 -> SharedFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}