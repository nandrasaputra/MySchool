package com.nandra.myschool.ui.add_new_contact

import android.app.Application
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
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.AddContactToRoasterState
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.coroutines.Job
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

    fun addContactToRoaster(contact: IRainbowContact, adapterPosition: Int) {
        if (addContactToRoasterJob != null) {
            if (addContactToRoasterJob!!.isActive) {
                return
            }
        }
        addContactToRoasterJob = viewModelScope.launch {
            _addContactToRoasterState.postValue(AddContactToRoasterState.Loading(adapterPosition))
            RainbowSdk.instance().contacts().addRainbowContactToRoster(contact, object : IRainbowSentInvitationListener {
                override fun onInvitationError() {
                    _addContactToRoasterState.postValue(AddContactToRoasterState.Failed( "Unknown Error", adapterPosition))
                }

                override fun onInvitationSentError(execption: RainbowServiceException?) {
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
                    _addContactToRoasterState.postValue(AddContactToRoasterState.Failed(errorMessage, adapterPosition))
                }

                override fun onInvitationSentSuccess() {
                    _addContactToRoasterState.postValue(AddContactToRoasterState.Finished(adapterPosition, Utility.nameBuilder(contact)))
                }
            })
        }
    }
}