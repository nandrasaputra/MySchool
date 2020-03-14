package com.nandra.myschool.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ale.listener.SignoutResponseListener
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.nandra.myschool.R
import com.nandra.myschool.ui.LoginActivity
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_profile_my_logout_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            RainbowSdk.instance().connection().signout(object : SignoutResponseListener() {
                override fun onSignoutSucceeded() {}
            })
            val intent = Intent(activity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(fragment_profile_toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        setupView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupView() {
        val contact = RainbowSdk.instance().myProfile().connectedUser
        val fullName = Utility.nameBuilder(contact)
        val email = contact.mainEmailAddress

        fragment_profile_person_name.text = fullName
        fragment_profile_person_email.text = email
        Glide.with(this)
            .load(contact.photo)
            .placeholder(R.drawable.ic_profile)
            .into(fragment_profile_my_photo)
    }
}