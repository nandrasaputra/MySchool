package com.nandra.myschool.ui.main.chat

import android.os.Bundle
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
import com.nandra.myschool.ui.main.MainActivityViewModel
import com.nandra.myschool.utils.Utility.ConnectServerState
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.connectServerState.observe(viewLifecycleOwner, Observer {
            handleConnectServerState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactListAdapter = ContactListAdapter()
        fragment_chat_contact_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = contactListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterListeners()
        RainbowSdk.instance().contacts().rainbowContacts.unregisterChangeListener(changeListener)
    }

    private fun handleConnectServerState(state: ConnectServerState) {
        when(state) {
            ConnectServerState.LOADING -> { }
            ConnectServerState.SUCCESS -> { getContactList() }
            else -> { }
        }
    }

    private fun getContactList() {
        unregisterListeners()
        chatViewModel.updateContactList()
        contactList = chatViewModel.getContactList()
        registerListeners()

        activity?.runOnUiThread {
            contactListAdapter.submitList(contactList)
            contactListAdapter.notifyDataSetChanged()
        }
    }

    // Unregister IRainbowContact.IContactListener to all contacts
    private fun unregisterListeners() {
        for (contact in contactList) {
            contact.unregisterChangeListener(this)
        }
    }

    // Register IRainbowContact.IContactListener for all contacts
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
        getContactList()
    }
}