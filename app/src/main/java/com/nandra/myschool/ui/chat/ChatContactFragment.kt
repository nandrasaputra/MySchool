package com.nandra.myschool.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence
import com.ale.infra.list.IItemListChangeListener
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ContactListAdapter
import com.nandra.myschool.ui.MainActivityViewModel
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.LoadingState
import kotlinx.android.synthetic.main.chat_contact_fragment.*

class ChatContactFragment : Fragment(), IRainbowContact.IContactListener {

    private lateinit var contactListAdapter: ContactListAdapter
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()
    private var contactList = listOf<IRainbowContact>()
    private val changeListener = IItemListChangeListener(::getContactList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RainbowSdk.instance().contacts().rainbowContacts.registerChangeListener(changeListener)
        return inflater.inflate(R.layout.chat_contact_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterListeners()
        RainbowSdk.instance().contacts().rainbowContacts.unregisterChangeListener(changeListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.loadingState.observe(viewLifecycleOwner, Observer {
            handleLoadingState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactListAdapter = ContactListAdapter(activity!!)
        fragment_chat_contact_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = contactListAdapter
        }
    }

    private fun handleLoadingState(state: LoadingState) {
        when(state) {
            LoadingState.LOADING -> {
                Log.d(LOG_DEBUG_TAG, "STATE LOADING")
            }
            LoadingState.SUCCESS -> {
                Log.d(LOG_DEBUG_TAG, "STATE FINISHED")
                getContactList()
            }
            else -> {
                Log.d(LOG_DEBUG_TAG, "STATE ERROR")
            }
        }
    }

    private fun getContactList() {
        unregisterListeners()
        contactList = chatViewModel.getContactList()
        registerListeners()

        activity?.runOnUiThread {
            contactListAdapter.submitList(contactList)
        }
    }

    // Unregister listener to all contacts
    private fun unregisterListeners() {
        for (contact in contactList) {
            contact.unregisterChangeListener(this)
        }
    }

    // Register listener for all contacts
    private fun registerListeners() {
        for (contact in contactList) {
            contact.registerChangeListener(this)
        }
    }

    override fun onCompanyChanged(p0: String?) {

    }

    override fun onPresenceChanged(p0: IRainbowContact?, p1: RainbowPresence?) {

    }

    override fun onActionInProgress(p0: Boolean) {

    }

    override fun contactUpdated(p0: IRainbowContact?) {

    }
}