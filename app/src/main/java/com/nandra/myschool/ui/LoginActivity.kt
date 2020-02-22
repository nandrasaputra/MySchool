package com.nandra.myschool.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.auth.FirebaseAuth
import com.nandra.myschool.R
import com.nandra.myschool.ui.main.MainActivity
import com.nandra.myschool.utils.RainbowConnection
import com.nandra.myschool.utils.RainbowConnectionListener
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity(), RainbowConnectionListener.Login, RainbowConnectionListener.Connection {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.login_activity)

        firebaseAuth = FirebaseAuth.getInstance()

        activity_login_button.setOnClickListener {
            setViewOnLoading(true)
            RainbowConnection.startConnection(this)
        }

        if (!RainbowSdk.instance().myProfile().userLoginInCache.isNullOrEmpty() and
            !RainbowSdk.instance().myProfile().userPasswordInCache.isNullOrEmpty() and
            (firebaseAuth.currentUser != null)) {

            activity_login_email_text.setText(RainbowSdk.instance().myProfile().userLoginInCache)
            activity_login_password_text.setText(RainbowSdk.instance().myProfile().userPasswordInCache)

            setViewOnLoading(true)
            RainbowConnection.startConnection(this)
        }
    }

    override fun onConnectionSuccess() {
        val typedEmail = getTypedEmail()
        val typedPassword = getTypedPassword()
        if (typedEmail.isNotEmpty() and typedPassword.isNotEmpty()) {
            RainbowConnection.startSignIn(typedEmail, typedPassword, this)
        } else {
            Toast.makeText(this, "Email or Password Cannot Empty!", Toast.LENGTH_SHORT).show()
            setViewOnLoading(false)
        }
    }

    override fun onConnectionFailed(error: String) {
        setViewOnLoading(false)
        Toast.makeText(this, "Rainbow Connection Failed: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onSignInSuccess() {
        startFirebaseAuthentication()
    }

    override fun onSignInFailed(error: String) {
        setViewOnLoading(false)
        Toast.makeText(this, "Rainbow Sign In Failed: $error", Toast.LENGTH_SHORT).show()
    }

    private fun startFirebaseAuthentication() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            if (user.email == getTypedEmail()) {
                proceedToMainActivity()
            } else {
                firebaseAuth.signOut()
                firebaseSignIn(getTypedEmail(), getTypedPassword())
            }
        } else {
            firebaseSignIn(getTypedEmail(), getTypedPassword())
        }
    }

    private fun firebaseSignIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                proceedToMainActivity()
            } else {
                setViewOnLoading(false)
                Toast.makeText(this, "Firebase Sign In Failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun proceedToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun getTypedEmail() = activity_login_email_text.text.toString()
    private fun getTypedPassword() = activity_login_password_text.text.toString()

    private fun setViewOnLoading(state: Boolean) {
        if (state) {
            activity_login_email_text.isEnabled = false
            activity_login_password_text.isEnabled = false
            activity_login_button.visibility = View.GONE
            activity_login_progress_bar.visibility = View.VISIBLE
        } else {
            activity_login_email_text.isEnabled = true
            activity_login_password_text.isEnabled = true
            activity_login_button.visibility = View.VISIBLE
            activity_login_progress_bar.visibility = View.GONE
        }
    }
}