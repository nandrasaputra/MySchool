package com.nandra.myschool.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChatViewPagerAdapter
import com.nandra.myschool.adapter.ClassroomDetailViewPagerAdapter
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.chat_detail_activity.*
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.classroom_detail_activity.*

class ClassroomDetailActivity : AppCompatActivity() {

    private lateinit var classroomDetailViewPagerAdapter: ClassroomDetailViewPagerAdapter
    private var subjectID: Int = -1
    private lateinit var subjectName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom_detail_activity)

        subjectID = intent.getIntExtra(Utility.EXTRA_SUBJECT_ID, -1)
        subjectName = intent.getStringExtra(Utility.EXTRA_SUBJECT_NAME) ?: "Classroom Detail"

        setupToolbar()
        setupViewPager()
    }

    private fun setupViewPager() {
        classroomDetailViewPagerAdapter = ClassroomDetailViewPagerAdapter(
            supportFragmentManager,
            activity_classroom_detail_tab_layout.tabCount
        )
        activity_classroom_detail_viewpager.adapter = classroomDetailViewPagerAdapter

        activity_classroom_detail_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                activity_classroom_detail_viewpager.currentItem = tab!!.position
            }
        })
        activity_classroom_detail_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(activity_classroom_detail_tab_layout))
    }

    private fun setupToolbar() {
        setSupportActionBar(activity_classroom_detail_toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        activity_classroom_detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        activity_classroom_detail_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        activity_classroom_detail_toolbar_title.text = subjectName
    }
}