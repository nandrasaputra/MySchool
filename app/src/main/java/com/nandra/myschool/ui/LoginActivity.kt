package com.nandra.myschool.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.RainbowConnection
import com.nandra.myschool.RainbowConnectionListener
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity(), RainbowConnectionListener.Login, RainbowConnectionListener.Connection {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        if (!RainbowSdk.instance().myProfile().userLoginInCache.isNullOrEmpty())
            activity_login_email_text.setText(RainbowSdk.instance().myProfile().userLoginInCache)

        if (!RainbowSdk.instance().myProfile().userPasswordInCache.isNullOrEmpty())
            activity_login_password_text.setText(RainbowSdk.instance().myProfile().userPasswordInCache)

        activity_login_btn.setOnClickListener {
            RainbowConnection.startConnection(this)
        }
    }

    override fun onConnectionSuccess() {
        val email = activity_login_email_text.text.toString()
        val password = activity_login_password_text.text.toString()
        RainbowConnection.startSignIn(email, password, this)
    }

    override fun onConnectionFailed(error: String) {
        Toast.makeText(this, "Connection Failed: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onSignInSuccess() {
        Toast.makeText(this, "Sign In Success", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    override fun onSignInFailed(error: String) {
        Toast.makeText(this, "Sign In Failed: $error", Toast.LENGTH_SHORT).show()
    }
}