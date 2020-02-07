package com.nandra.myschool.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility.EXTRA_JID
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.android.synthetic.main.chat_detail_activity.*

class ChatDetailActivity : AppCompatActivity() {

    lateinit var conversation: IRainbowConversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_detail_activity)

        setupToolbar()

        val jid = intent.getStringExtra(EXTRA_JID)
        conversation = RainbowSdk.instance().conversations().getConversationFromJid(jid)
        setupView(conversation)
    }

    private fun setupView(conversation: IRainbowConversation) {
        if (!conversation.isRoomType) {
            val contact = conversation.contact
            activity_chat_detail_name.text = nameBuilder(contact)

            Glide.with(this)
                .load(contact.photo)
                .into(activity_chat_detail_photo)
        } else {
            val room = conversation.room
            activity_chat_detail_name.text = room.name

            Glide.with(this)
                .load(room.photo)
                .into(activity_chat_detail_photo)
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(activity_detail_chat_toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        activity_detail_chat_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        activity_detail_chat_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}