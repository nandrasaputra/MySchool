package com.nandra.myschool.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private var cachedConversationList: List<IRainbowConversation>? = null
    private var cachedContactList: List<IRainbowContact>? = null

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