package com.nandra.myschool.ui

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ListAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.IMMessage
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChatDetailListAdapter
import com.nandra.myschool.utils.Utility.EXTRA_JID
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.android.synthetic.main.chat_detail_activity.*

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var conversation: IRainbowConversation
    private lateinit var chatDetailListAdapter: ChatDetailListAdapter
    private val changeListener = IItemListChangeListener(::updateMessageList)
    private var messageList = listOf<IMMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_detail_activity)

        setupToolbar()

        val jid = intent.getStringExtra(EXTRA_JID)
        conversation = RainbowSdk.instance().conversations().getConversationFromJid(jid)

        setupView(conversation)

        conversation.messages.registerChangeListener(changeListener)

        retriveMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        conversation.messages.unregisterChangeListener(changeListener)
    }

    private fun setupView(conversation: IRainbowConversation) {

        chatDetailListAdapter = ChatDetailListAdapter()
        activity_chat_detail_recycler_view.apply {
            adapter = chatDetailListAdapter
            layoutManager = LinearLayoutManager(this@ChatDetailActivity)
        }

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

    private fun retriveMessage() {
        RainbowSdk.instance().im().getMessagesFromConversation(conversation, 50)
    }

    private fun updateMessageList() {
        val newMessages = conversation.messages.copyOfDataList
        messageList = newMessages

        runOnUiThread {
            chatDetailListAdapter.submitList(messageList)
            chatDetailListAdapter.notifyDataSetChanged()
        }
    }

}