package com.nandra.myschool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nandra.myschool.ui.channel.ChannelAllFragment
import com.nandra.myschool.ui.channel.ChannelOwnedChannel
import com.nandra.myschool.ui.channel.ChannelSubscribedFragment

class ChannelViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val numberOfTab: Int
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChannelAllFragment()
            1 -> ChannelSubscribedFragment()
            2 -> ChannelOwnedChannel()
            else -> throw Exception()
        }
    }

    override fun getCount(): Int {
        return numberOfTab
    }
}