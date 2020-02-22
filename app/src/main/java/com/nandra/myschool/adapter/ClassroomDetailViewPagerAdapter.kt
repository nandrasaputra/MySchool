package com.nandra.myschool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nandra.myschool.ui.chat.ChatContactFragment
import com.nandra.myschool.ui.chat.ChatConversationFragment
import com.nandra.myschool.ui.classroom.ClassroomFeedFragment
import com.nandra.myschool.ui.classroom.ClassroomMaterialFragment
import com.nandra.myschool.ui.classroom.ClassroomSessionFragment

class ClassroomDetailViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val numberOfTab: Int
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ClassroomFeedFragment()
            1 -> ClassroomMaterialFragment()
            2 -> ClassroomSessionFragment()
            else -> throw Exception()
        }
    }

    override fun getCount(): Int {
        return numberOfTab
    }

}