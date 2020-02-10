package com.nandra.myschool.ui.channel

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelViewPagerAdapter
import kotlinx.android.synthetic.main.channel_fragment.*

class ChannelFragment : Fragment() {

    private lateinit var channelViewPagerAdapter: ChannelViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.channel_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(fragment_channel_toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        setupViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.channel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun setupViewPager() {
        channelViewPagerAdapter = ChannelViewPagerAdapter(
            childFragmentManager,
            fragment_channel_tab_layout.tabCount
        )
        fragment_channel_viewpager.adapter = channelViewPagerAdapter

        fragment_channel_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                fragment_channel_viewpager.currentItem = tab!!.position
            }
        })

        fragment_channel_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(fragment_channel_tab_layout))
    }
}