package com.nandra.myschool.ui.classroom_detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.ChannelItemBuilder
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.google.android.material.tabs.TabLayout
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomDetailViewPagerAdapter
import com.nandra.myschool.ui.AddNewChannelItemDialogFragment
import com.nandra.myschool.utils.Utility.IAddNewChannelItem
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.EXTRA_SUBJECT_ID
import com.nandra.myschool.utils.Utility.EXTRA_SUBJECT_NAME
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.classroom_detail_activity.*

class ClassroomDetailActivity : AppCompatActivity(), IAddNewChannelItem {

    private lateinit var classroomDetailViewPagerAdapter: ClassroomDetailViewPagerAdapter
    private var subjectID: Int = -1
    private lateinit var subjectName: String
    private val classroomDetailViewModel: ClassroomDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom_detail_activity)

        subjectID = intent.getIntExtra(EXTRA_SUBJECT_ID, -1)
        subjectName = intent.getStringExtra(EXTRA_SUBJECT_NAME) ?: "Classroom Detail"

        setupToolbar()
        setupViewPager()

        classroomDetailViewModel.detailSubjectDataLoadState.observe(this, Observer {
            handleDetailLoadState(it)
        })
        activity_classroom_detail_fab.setOnClickListener {
            AddNewChannelItemDialogFragment(this).show(supportFragmentManager, "DialogFragment")
        }
    }

    override fun onCancelButtonClicked() {
        Log.d(LOG_DEBUG_TAG, "Cancel Button Clicked")
    }

    override fun onSendButtonClicked(message: String) {
        Log.d(LOG_DEBUG_TAG, "Send Button Clicked, Message = $message")
        val channelItem = ChannelItemBuilder().setMessage(message).build()
        RainbowSdk.instance().channels().createItem(classroomDetailViewModel.currentChannel, channelItem, object : IChannelProxy.IChannelCreateItemListener {
            override fun onCreateMessageItemFailed(p0: RainbowServiceException?) {
                Log.d(LOG_DEBUG_TAG, "Create Item Failed")
            }

            override fun onCreateMessageItemSuccess(p0: String?) {
                classroomDetailViewModel.refreshChannelItemList()
                Log.d(LOG_DEBUG_TAG, "Create Item Success")
            }
        })
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
                activity_classroom_detail_fab.hide()
                handleFabAppearance(tab.position)
                activity_classroom_detail_fab.show()
            }
        })
        activity_classroom_detail_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(activity_classroom_detail_tab_layout))
        activity_classroom_detail_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                when(state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        activity_classroom_detail_fab.show()
                    }
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                        activity_classroom_detail_fab.hide()
                    }
                }
            }
        })
    }

    private fun handleFabAppearance(position: Int) {
        when(position) {
            0 -> { activity_classroom_detail_fab.setImageResource(R.drawable.ic_add_new_chat) }
            1 -> { activity_classroom_detail_fab.setImageResource(R.drawable.ic_cloud_upload) }
            2 -> { activity_classroom_detail_fab.setImageResource(R.drawable.ic_add)}
        }
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

    private fun handleDetailLoadState(state: DataLoadState) {
        when (state) {
            DataLoadState.UNLOADED -> {
                classroomDetailViewModel.getDetailSubject(subjectID)
            }
            DataLoadState.LOADING -> { }
            DataLoadState.LOADED -> {
                //DO SOMETHING
            }
            else -> {}
        }
    }
}