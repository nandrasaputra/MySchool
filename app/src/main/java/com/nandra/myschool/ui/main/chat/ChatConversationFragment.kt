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
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ConversationListAdapter
import com.nandra.myschool.ui.main.MainActivityViewModel
import com.nandra.myschool.utils.Utility.ChatFilterState
import com.nandra.myschool.utils.Utility.ConnectServerState
import kotlinx.android.synthetic.main.chat_conversation_fragment.*

class ChatConversationFragment : Fragment(), IRainbowContact.IContactListener {

    private lateinit var conversationListAdapter: ConversationListAdapter
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()
    private var conversationList = listOf<IRainbowConversation>()
    private var contactList = listOf<IRainbowContact>()
    private val changeListener = IItemListChangeListener(::getConversationList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RainbowSdk.instance().conversations().allConversations.registerChangeListener(changeListener)
        return inflater.inflate(R.layout.chat_conversation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        conversationListAdapter = ConversationListAdapter()
        fragment_chat_conversation_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = conversationListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterListeners()
        RainbowSdk.instance().contacts().rainbowContacts.unregisterChangeListener(changeListener)
    }

    override fun onCompanyChanged(p0: String?) {}

    override fun onPresenceChanged(p0: IRainbowContact?, p1: RainbowPresence?) {}

    override fun onActionInProgress(p0: Boolean) {}

    override fun contactUpdated(p0: IRainbowContact?) {
        getConversationList()
    }

    private fun getConversationList() {
        unregisterListeners()
        chatViewModel.updateConversationList()
        conversationList = chatViewModel.getConversationList()
        getContactFromConversation(conversationList)
        registerListeners()

        if (!chatViewModel.isSearchViewOpened) {
            activity?.runOnUiThread {
                conversationListAdapter.submitAndUpdateList(conversationList)
            }
        } else {
            activity?.runOnUiThread {
                conversationListAdapter.updateList(conversationList)
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

    private fun getContactFromConversation(conversations: List<IRainbowConversation>) {

        val newContact = mutableListOf<IRainbowContact>()

        for (conversation in conversations) {
            if (!conversation.isRoomType) {
                newContact.add(conversation.contact)
            }
        }

        contactList = newContact
    }

    private fun observeViewModel() {
        mainViewModel.connectServerState.observe(viewLifecycleOwner, Observer {
            handleConnectServerState(it)
        })
        chatViewModel.chatFilterState.observe(viewLifecycleOwner, Observer {
            handleChatFilterState(it)
        })
    }

    private fun handleConnectServerState(state: ConnectServerState) {
        when(state) {
            ConnectServerState.LOADING -> {
                fragment_chat_conversation_shimmer_layout.visibility = View.VISIBLE
                fragment_chat_conversation_shimmer_veil.visibility = View.VISIBLE
                fragment_chat_conversation_shimmer_layout.startShimmer()
            }
            ConnectServerState.SUCCESS -> {
                getConversationList()
                fragment_chat_conversation_shimmer_layout.visibility = View.GONE
                fragment_chat_conversation_shimmer_veil.visibility = View.GONE
                fragment_chat_conversation_shimmer_layout.stopShimmer()
            }
            else -> { }
        }
    }

    private fun handleChatFilterState(state: ChatFilterState) {
        when(state) {
            is ChatFilterState.FilterConversation -> {
                conversationListAdapter.filterState = ChatFilterState.FilterConversation(state.constraint)
                conversationListAdapter.filter.filter(state.constraint)
            }
            is ChatFilterState.NoFilter -> {
                conversationListAdapter.restoreList()
                conversationListAdapter.filterState = ChatFilterState.NoFilter
            }
            else -> {}
        }
    }
}