package com.nandra.myschool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nandra.myschool.ui.chat.ChatContactFragment
import com.nandra.myschool.ui.chat.ChatConversationFragment

class ChatViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val numberOfTab: Int
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatConversationFragment()
            1 -> ChatContactFragment()
            else -> throw Exception()
        }
    }

    override fun getCount(): Int {
        return numberOfTab
    }
}