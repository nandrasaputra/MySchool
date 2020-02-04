package com.nandra.myschool.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ale.infra.contact.IRainbowContact
import com.ale.rainbowsdk.RainbowSdk

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    fun getContactList() : List<IRainbowContact> {
        return RainbowSdk.instance().contacts().rainbowContacts.copyOfDataList
    }

}