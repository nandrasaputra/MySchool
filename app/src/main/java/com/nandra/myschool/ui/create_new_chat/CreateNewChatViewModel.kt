package com.nandra.myschool.ui.create_new_chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ale.infra.contact.IRainbowContact
import com.ale.rainbowsdk.RainbowSdk

class CreateNewChatViewModel(app: Application) : AndroidViewModel(app) {

    private var cachedContactList: List<IRainbowContact>? = null

    fun getContactList() : List<IRainbowContact> {
        return if(cachedContactList != null) {
            cachedContactList!!
        } else {
            updateContactList()
            cachedContactList!!
        }
    }

    fun updateContactList() {
        cachedContactList = RainbowSdk.instance().contacts().rainbowContacts.copyOfDataList
    }
}