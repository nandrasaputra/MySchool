package com.nandra.myschool.utils

import com.ale.infra.contact.IRainbowContact

object Utility {
    const val EXTRA_MESSAGE = "message"
    const val EXTRA_JID = "jid"
    const val LOG_DEBUG_TAG = "MySchool"

    enum class LoadingState {
        LOADING,
        SUCCESS,
        CONNECTION_ERROR
    }

    enum class NetworkState {
        CONNECTED,
        DISCONNECTED,
        UNAVAILABLE
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