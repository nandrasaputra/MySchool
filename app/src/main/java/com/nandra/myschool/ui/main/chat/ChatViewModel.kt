package com.nandra.myschool.ui.main.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility.ChatFilterState

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private var cachedConversationList: List<IRainbowConversation>? = null
    private var cachedContactList: List<IRainbowContact>? = null

    val chatFilterState: LiveData<ChatFilterState>
        get() = _chatFilterState
    private val _chatFilterState = MutableLiveData<ChatFilterState>(ChatFilterState.NoFilter)

    var isSearchViewOpened = false

    fun changeChatFilterState(state: ChatFilterState) {
        _chatFilterState.value = state
    }

    fun getContactList() : List<IRainbowContact> {
        return if(cachedContactList != null) {
            cachedContactList!!
        } else {
            updateContactList()
            cachedContactList!!
        }
    }

    fun getConversationList() : List<IRainbowConversation> {
        return if(cachedConversationList != null) {
            cachedConversationList!!
        } else {
            updateConversationList()
            cachedConversationList!!
        }
    }

    fun updateContactList() {
        cachedContactList = RainbowSdk.instance().contacts().rainbowContacts.copyOfDataList
    }

    fun updateConversationList() {
        cachedConversationList = RainbowSdk.instance().conversations().allConversations.copyOfDataList
    }
}