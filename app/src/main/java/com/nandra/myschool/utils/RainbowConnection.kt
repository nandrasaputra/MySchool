package com.nandra.myschool.utils

import android.os.Handler
import android.os.Looper
import com.ale.listener.SigninResponseListener
import com.ale.listener.StartResponseListener
import com.ale.rainbowsdk.RainbowSdk

object RainbowConnection {

    fun startConnection(connection: RainbowConnectionListener.Connection) {
        RainbowSdk.instance().connection().start(object : StartResponseListener() {
            override fun onRequestFailed(errorCode: RainbowSdk.ErrorCode?, text: String) {
                Handler(Looper.getMainLooper())
                    .post{connection.onConnectionFailed(text)}
            }

            override fun onStartSucceeded() {
                Handler(Looper.getMainLooper())
                    .post(connection::onConnectionSuccess)
            }

        })
    }

    fun startSignIn(email: String, password: String, login: RainbowConnectionListener.Login) {
        RainbowSdk.instance().connection().signin(
            email, password, "sandbox.openrainbow.com", object : SigninResponseListener() {
                override fun onRequestFailed(errorCode: RainbowSdk.ErrorCode?, text: String) {
                    Handler(Looper.getMainLooper())
                        .post{login.onSignInFailed(text)}
                }

                override fun onSigninSucceeded() {
                    Handler(Looper.getMainLooper())
                        .post(login::onSignInSuccess)
                }
            }
        )
    }

}