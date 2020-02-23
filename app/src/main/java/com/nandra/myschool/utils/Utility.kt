package com.nandra.myschool.utils

import com.ale.infra.contact.IRainbowContact
import java.text.SimpleDateFormat
import java.util.*

object Utility {
    const val EXTRA_SUBJECT_NAME = "subject_name"
    const val EXTRA_SUBJECT_ID = "subject_code"
    const val EXTRA_JID = "jid"
    const val LOG_DEBUG_TAG = "MySchool"

    enum class ConnectServerState {
        LOADING,
        SUCCESS,
        CONNECTION_ERROR
    }

    enum class DataLoadState {
        LOADED,
        UNLOADED,
        LOADING,
        ERROR
    }

    enum class NetworkState {
        CONNECTED,
        DISCONNECTED,
        UNAVAILABLE
    }

    fun Date.convertToString() : String {
        val simpleDateFormat = SimpleDateFormat("hh:mm a")
        return simpleDateFormat.format(this)
    }

    fun nameBuilder(contact: IRainbowContact) : String {
        val firstName = contact.firstName
        val lastName = contact.lastName
        return if(firstName!= null && lastName != null) {
            "$firstName $lastName"
        } else if (firstName != null && lastName == null) {
            firstName
        } else if(firstName == null && lastName != null) {
            lastName
        } else {
            "No Name"
        }
    }
}