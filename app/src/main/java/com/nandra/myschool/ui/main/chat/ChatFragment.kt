package com.nandra.myschool.ui.main.chat

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.core.utilities.Utilities
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChatViewPagerAdapter
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.main_activity.*

class ChatFragment : Fragment() {

    private lateinit var chatViewPagerAdapter: ChatViewPagerAdapter
    private var searchView: SearchView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(fragment_chat_toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        setupViewPager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)

        val searchItem = menu.findItem(R.id.chat_search)
        searchView = searchItem?.actionView as SearchView

        searchView?.run {
            this.isSubmitButtonEnabled = true

            this.setOnSearchClickListener {
                fragment_chat_tab_layout.visibility = View.GONE
                fragment_chat_toolbar_title.visibility = View.GONE

                when(fragment_chat_tab_layout.selectedTabPosition) {
                    0 -> {this.queryHint = "Search Conversation"}
                    1 -> {this.queryHint = "Search Contact"}
                }
            }

            this.setOnCloseListener {
                fragment_chat_tab_layout.visibility = View.VISIBLE
                fragment_chat_toolbar_title.visibility = View.VISIBLE
                false
            }

            /*this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    TODO("Not yet implemented")
                }
            })*/
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupViewPager() {
        chatViewPagerAdapter = ChatViewPagerAdapter(
            childFragmentManager,
            fragment_chat_tab_layout.tabCount
        )
        fragment_chat_viewpager.adapter = chatViewPagerAdapter

        fragment_chat_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                fragment_chat_viewpager.currentItem = tab!!.position
                fragment_chat_fab.hide()
                handleFabAppearance(tab.position)
                fragment_chat_fab.show()
            }
        })
        fragment_chat_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(fragment_chat_tab_layout))
        fragment_chat_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                when(state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        fragment_chat_fab.show()
                    }
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                        fragment_chat_fab.hide()
                    }
                }
            }
        })
    }

    private fun handleFabAppearance(position: Int) {
        when(position) {
            0 -> { fragment_chat_fab.setImageResource(R.drawable.ic_add_new_chat) }
            1 -> { fragment_chat_fab.setImageResource(R.drawable.ic_person_add) }
        }
    }
}