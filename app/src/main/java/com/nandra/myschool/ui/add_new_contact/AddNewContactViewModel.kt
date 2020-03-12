package com.nandra.myschool.ui.add_new_contact

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.infra.contact.Contact
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.listener.IRainbowContactsSearchListener
import com.ale.listener.IRainbowSentInvitationListener
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.AddContactToRoasterState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddNewContactViewModel(app: Application) : AndroidViewModel(app) {

    var contactList = listOf<IRainbowContact>()
    val addNewContactContentLoadState: LiveData<DataLoadState>
        get() = _addNewContactContentLoadState
    private val _addNewContactContentLoadState = MutableLiveData(DataLoadState.UNLOADED)

    val addContactToRoasterState: LiveData<AddContactToRoasterState>
        get() = _addContactToRoasterState
    private val _addContactToRoasterState = MutableLiveData<AddContactToRoasterState>(AddContactToRoasterState.Idle)
    private var addContactToRoasterJob: Job? = null

    fun searchContact(query: String) {
        RainbowSdk.instance().contacts().searchByName(query, object : IRainbowContactsSearchListener {
            override fun searchStarted() {
                _addNewContactContentLoadState.postValue(DataLoadState.LOADING)
            }

            override fun searchError(exception: RainbowServiceException?) {
                _addNewContactContentLoadState.postValue(DataLoadState.ERROR)
            }

            override fun searchFinished(newList: MutableList<Contact>?) {
                contactList = newList ?: listOf()
                _addNewContactContentLoadState.postValue(DataLoadState.LOADED)
            }
        })
    }

    fun resetAddContactToRoasterState() {
        _addContactToRoasterState.value = AddContactToRoasterState.Idle
    }

    fun resetLoadState() {
        _addNewContactContentLoadState.value = DataLoadState.UNLOADED
    }

    fun addContactToRoaster(contact: IRainbowContact) {
        if (addContactToRoasterJob != null) {
            if (addContactToRoasterJob!!.isActive) {
                return
            }
        }
        addContactToRoasterJob = viewModelScope.launch {
            _addContactToRoasterState.postValue(AddContactToRoasterState.Loading)
            delay(3000L)
            RainbowSdk.instance().contacts().addRainbowContactToRoster(contact, object : IRainbowSentInvitationListener {
                override fun onInvitationError() {
                    Log.d(LOG_DEBUG_TAG, "onInvitationError")
                    _addContactToRoasterState.postValue(AddContactToRoasterState.Failed( "Unknown Error"))
                }

                override fun onInvitationSentError(execption: RainbowServiceException?) {
                    Log.d(LOG_DEBUG_TAG, "onInvitationSentError : Detail Code = ${execption?.detailsCode}, message = ${execption?.detailsMessage}")
                    var errorMessage = "Failed To Add Contact"
                    execption?.run {
                        when(this.detailsCode) {
                            409603 -> {
                                errorMessage = "Invitation Already Sent, Please Try Again Later"
                            }
                            409602 -> {
                                errorMessage = "This Person Already In The Contact"
                            }
                            else -> {}
                        }
                    }
                    _addContactToRoasterState.postValue(AddContactToRoasterState.Failed(errorMessage))
                }

                override fun onInvitationSentSuccess() {
                    Log.d(LOG_DEBUG_TAG, "onInvitationSentSuccess")
                    _addContactToRoasterState.postValue(AddContactToRoasterState.Finished)
                }
            })
        }
    }
}