package com.nandra.myschool.utils

class RainbowConnectionListener {

    interface Connection {
        fun onConnectionSuccess()
        fun onConnectionFailed(error: String)
    }

    interface Login {
        fun onSignInSuccess()
        fun onSignInFailed(error: String)
    }
}