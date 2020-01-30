package com.nandra.myschool

import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence

class RainbowConnectionListener {
    abstract class Contact : IRainbowContact.IContactListener {
        override fun onCompanyChanged(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPresenceChanged(p0: IRainbowContact?, p1: RainbowPresence?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onActionInProgress(p0: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun contactUpdated(p0: IRainbowContact?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        abstract fun contactUpdated()
    }

    interface Connection {
        fun onConnectionSuccess()
        fun onConnectionFailed(error: String)
    }

    interface Login {
        fun onSignInSuccess()
        fun onSignInFailed(error: String)
    }
}