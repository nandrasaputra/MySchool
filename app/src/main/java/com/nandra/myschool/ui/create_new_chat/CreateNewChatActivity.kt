package com.nandra.myschool.ui.create_new_chat

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence
import com.ale.infra.list.IItemListChangeListener
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.CreateNewChatListAdapter
import kotlinx.android.synthetic.main.create_new_chat_activity.*

class CreateNewChatActivity : AppCompatActivity(), IRainbowContact.IContactListener {

    private lateinit var createNewChatListAdapter: CreateNewChatListAdapter
    private val createNewChatViewModel: CreateNewChatViewModel by viewModels()
    private var contactList = listOf<IRainbowContact>()
    private val changeListener = IItemListChangeListener(::getContactList)
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_new_chat_activity)

        RainbowSdk.instance().contacts().rainbowContacts.registerChangeListener(changeListener)

        setupToolbar()
        setupView()
        observeViewModel()

        getContactList()
    }

    override fun onCompanyChanged(p0: String?) {}

    override fun onPresenceChanged(p0: IRainbowContact?, p1: RainbowPresence?) {}

    override fun onActionInProgress(p0: Boolean) {}

    override fun contactUpdated(p0: IRainbowContact?) { getContactList() }

    private fun observeViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterListeners()
        RainbowSdk.instance().contacts().rainbowContacts.unregisterChangeListener(changeListener)
    }

    private fun setupToolbar() {
        setSupportActionBar(activity_create_new_chat_toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        activity_create_new_chat_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        activity_create_new_chat_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        createNewChatListAdapter = CreateNewChatListAdapter()
        activity_create_new_chat_recycler_view.apply {
            adapter = createNewChatListAdapter
            layoutManager = LinearLayoutManager(this@CreateNewChatActivity)
        }
    }

    private fun getContactList() {
        unregisterListeners()
        createNewChatViewModel.updateContactList()
        contactList = createNewChatViewModel.getContactList()
        registerListeners()

        if (searchView != null) {
            if (searchView!!.isIconified) {
                //SearchView Closed
                runOnUiThread {
                    createNewChatListAdapter.submitAndUpdateList(contactList)
                    activity_create_new_chat_contact_count.text = "${contactList.size} Contact"
                }
            } else {
                //SearchView Opened
                runOnUiThread {
                    createNewChatListAdapter.updateList(contactList)
                }
            }
        } else {
            runOnUiThread {
                createNewChatListAdapter.submitAndUpdateList(contactList)
                activity_create_new_chat_contact_count.text = "${contactList.size} Contact"
            }
        }
    }

    private fun unregisterListeners() {
        for (contact in contactList) {
            contact.unregisterChangeListener(this)
        }
    }

    private fun registerListeners() {
        for (contact in contactList) {
            contact.registerChangeListener(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_new_chat_menu, menu)
        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu?) {
        val searchItem = menu?.findItem(R.id.create_new_chat_search_menu_item)
        searchView = searchItem?.actionView as SearchView

        searchView?.run {

            this.setOnSearchClickListener {
                activity_create_new_chat_title_layout.visibility = View.GONE
                this.queryHint = "Search Contact"
            }

            this.setOnCloseListener {
                activity_create_new_chat_title_layout.visibility = View.VISIBLE
                createNewChatListAdapter.restoreList()
                false
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.run {
                        createNewChatListAdapter.filter.filter(this.toLowerCase())
                    }
                    return true
                }
            })
        }
    }

}