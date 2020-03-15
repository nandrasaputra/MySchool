package com.nandra.myschool.ui.main.chat

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChatViewPagerAdapter
import com.nandra.myschool.ui.add_new_contact.AddNewContactActivity
import com.nandra.myschool.ui.create_new_chat.CreateNewChatActivity
import com.nandra.myschool.utils.Utility.ChatFilterState
import kotlinx.android.synthetic.main.chat_fragment.*

class ChatFragment : Fragment() {

    private lateinit var chatViewPagerAdapter: ChatViewPagerAdapter
    private var searchView: SearchView? = null
    private val chatViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_chat_fab.setOnClickListener {
            when(fragment_chat_tab_layout.selectedTabPosition) {
                0 -> {
                    val intent = Intent(view.context, CreateNewChatActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    }
                    view.context.startActivity(intent)
                }
                1 -> {
                    val intent = Intent(view.context, AddNewContactActivity::class.java)
                    view.context.startActivity(intent)
                }
            }
        }
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
        setupSearchView(menu)
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
                attemptHideSearchView()
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

    private fun setupSearchView(menu: Menu) {

        chatViewModel.isSearchViewOpened = false
        chatViewModel.changeChatFilterState(ChatFilterState.NoFilter)

        val searchItem = menu.findItem(R.id.chat_search_menu_item)
        searchView = searchItem?.actionView as SearchView

        searchView?.run {
            this.setOnSearchClickListener {
                chatViewModel.isSearchViewOpened = true
                fragment_chat_toolbar_title.visibility = View.GONE
                when (fragment_chat_tab_layout.selectedTabPosition) {
                    0 -> {
                        this.queryHint = "Search In Conversation"
                    }
                    1 -> {
                        this.queryHint = "Search In Contact"
                    }
                }
            }

            this.setOnCloseListener {
                fragment_chat_toolbar_title.visibility = View.VISIBLE
                chatViewModel.changeChatFilterState(ChatFilterState.NoFilter)
                chatViewModel.isSearchViewOpened = false
                false
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.run {
                        //createNewChatListAdapter.filter.filter(this.toLowerCase())
                        when (fragment_chat_tab_layout.selectedTabPosition) {
                            0 -> {
                                chatViewModel.changeChatFilterState(ChatFilterState.FilterConversation(newText))
                            }
                            1 -> {
                                chatViewModel.changeChatFilterState(ChatFilterState.FilterContact(newText))
                            }
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun attemptHideSearchView() {
        searchView?.run {
            if (!this.isIconified) {
                this.isIconified = true
                if (!this.isIconified) {
                    this.isIconified = true
                }
            }
        }
    }
}