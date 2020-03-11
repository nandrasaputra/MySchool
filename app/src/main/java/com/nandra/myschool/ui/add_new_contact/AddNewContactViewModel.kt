package com.nandra.myschool.ui.add_new_contact

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ale.infra.contact.Contact
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.listener.IRainbowContactsListener
import com.ale.listener.IRainbowContactsSearchListener
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG

class AddNewContactViewModel(app: Application) : AndroidViewModel(app) {

    var contactList = listOf<IRainbowContact>()
    val addNewContactLoadState: LiveData<DataLoadState>
        get() = _addNewContactLoadState
    private val _addNewContactLoadState = MutableLiveData(DataLoadState.UNLOADED)

    fun searchContact(query: String) {
        Log.d(LOG_DEBUG_TAG, "ViewModel -> searchContact ($query)")
        RainbowSdk.instance().contacts().searchByName(query, object : IRainbowContactsSearchListener {
            override fun searchStarted() {
                _addNewContactLoadState.postValue(DataLoadState.LOADING)
            }

            override fun searchError(exception: RainbowServiceException?) {
                _addNewContactLoadState.postValue(DataLoadState.ERROR)
            }

            override fun searchFinished(newList: MutableList<Contact>?) {
                contactList = newList ?: listOf()
                _addNewContactLoadState.postValue(DataLoadState.LOADED)
            }
        })
    }

    fun resetLoadState() {
        _addNewContactLoadState.value = DataLoadState.UNLOADED
    }
}