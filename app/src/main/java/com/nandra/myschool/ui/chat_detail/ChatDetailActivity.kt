package com.nandra.myschool.ui.chat_detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.IMMessage
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.listener.IRainbowImListener
import com.ale.rainbowsdk.RainbowSdk
import com.bumptech.glide.Glide
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChatDetailListAdapter
import com.nandra.myschool.utils.Utility.EXTRA_JID
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.android.synthetic.main.chat_detail_activity.*

class ChatDetailActivity : AppCompatActivity(), IRainbowContact.IContactListener, IRainbowImListener {

    private lateinit var conversation: IRainbowConversation
    private lateinit var chatDetailListAdapter: ChatDetailListAdapter
    private val changeListener = IItemListChangeListener(::updateMessageList)
    private var messageList = listOf<IMMessage>()
    private var currentContact : IRainbowContact? = null
    private var currentAppearance: RainbowPresence = RainbowPresence.OFFLINE
    private var isOnTypingState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_detail_activity)

        setupToolbar()

        val jid = intent.getStringExtra(EXTRA_JID)

        conversation = RainbowSdk.instance().conversations().getConversationFromJid(jid)
        activity_chat_detail_shimmer_layout.visibility = View.VISIBLE
        activity_chat_detail_shimmer_veil.visibility = View.VISIBLE
        activity_chat_detail_shimmer_layout.startShimmer()

        setupView(conversation)

        conversation.messages.registerChangeListener(changeListener)
        RainbowSdk.instance().im().registerListener(this)
        retrieveMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        conversation.messages.unregisterChangeListener(changeListener)
        RainbowSdk.instance().im().unregisterListener(this)
        unregisterContactChangeListener()
    }

    private fun setupView(conversation: IRainbowConversation) {

        chatDetailListAdapter = ChatDetailListAdapter()
        activity_chat_detail_recycler_view.apply {
            adapter = chatDetailListAdapter
            layoutManager = LinearLayoutManager(this@ChatDetailActivity)
        }

        activity_chat_message_send_button.setOnClickListener {
            sendMessage()
        }

        if (!conversation.isRoomType) {
            val contact = conversation.contact
            contact?.run {
                registerContactChangeListener(this)
                handlePresenceChange(this.presence)
                activity_chat_detail_name.text = nameBuilder(this)

                Glide.with(this@ChatDetailActivity)
                    .load(contact.photo)
                    .into(activity_chat_detail_photo)
            }
        } else {
            val room = conversation.room
            activity_chat_detail_name.text = room.name

            Glide.with(this)
                .load(room.photo)
                .into(activity_chat_detail_photo)
        }

        activity_chat_message_edit_text.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (charSequence.isNotEmpty())
                    RainbowSdk.instance().im().sendIsTyping(conversation, true)
                else
                    RainbowSdk.instance().im().sendIsTyping(conversation, false)
            }
        })

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

    private fun retrieveMessage() {
        RainbowSdk.instance().im().getMessagesFromConversation(conversation, 50)
    }

    private fun updateMessageList() {

        val newMessages = conversation.messages.copyOfDataList
        messageList = newMessages

        runOnUiThread {
            chatDetailListAdapter.submitList(messageList)
            chatDetailListAdapter.notifyDataSetChanged()
            scrollToBottom()
            RainbowSdk.instance().im().markMessagesFromConversationAsRead(conversation)

            activity_chat_detail_shimmer_layout.visibility = View.GONE
            activity_chat_detail_shimmer_veil.visibility = View.GONE
            activity_chat_detail_shimmer_layout.stopShimmer()
        }
    }

    private fun sendMessage() {
        if (activity_chat_message_edit_text.text.toString().isNotEmpty())
        RainbowSdk.instance().im().sendMessageToConversation(conversation, activity_chat_message_edit_text.text.toString())
        activity_chat_message_edit_text.setText("")
    }

    private fun scrollToBottom() {
        activity_chat_detail_recycler_view.scrollToPosition(chatDetailListAdapter.itemCount - 1)
    }

    private fun registerContactChangeListener(contact: IRainbowContact?) {
        contact?.run {
            currentContact = this

            this.registerChangeListener(this@ChatDetailActivity)
        }
    }

    private fun unregisterContactChangeListener() {
        currentContact?.run {
            this.unregisterChangeListener(this@ChatDetailActivity)
        }
    }

    override fun onCompanyChanged(p0: String?) {}

    override fun onPresenceChanged(p0: IRainbowContact?, rainbowPresence: RainbowPresence?) {
        rainbowPresence?.run {
            handlePresenceChange(this)
        }
    }

    override fun onActionInProgress(p0: Boolean) {}

    override fun contactUpdated(p0: IRainbowContact?) {}

    private fun handlePresenceChange(rainbowPresence: RainbowPresence) {
        when(rainbowPresence) {
            RainbowPresence.OFFLINE -> {
                currentAppearance = RainbowPresence.OFFLINE
                if (!isOnTypingState) {
                    runOnUiThread {
                        activity_chat_detail_name_status.visibility = View.GONE
                    }
                }
            }
            RainbowPresence.ONLINE -> {
                currentAppearance = RainbowPresence.ONLINE
                if (!isOnTypingState) {
                    runOnUiThread {
                        activity_chat_detail_name_status.visibility = View.VISIBLE
                        activity_chat_detail_name_status.text = "Online"
                    }
                }
            }
            RainbowPresence.MOBILE_ONLINE -> {
                currentAppearance = RainbowPresence.MOBILE_ONLINE
                if (!isOnTypingState) {
                    runOnUiThread {
                        activity_chat_detail_name_status.visibility = View.VISIBLE
                        activity_chat_detail_name_status.text = "Mobile Online"
                    }
                }
            }
            else -> { }
        }
    }

    override fun onImSent(p0: String?, newMessage: IMMessage?) {}

    override fun onImReceived(p0: String?, newMessage: IMMessage?) {}

    override fun onMessagesListUpdated(p0: Int, p1: String?, newMessageList: MutableList<IMMessage>?) {
        Log.d(LOG_DEBUG_TAG, "Message List Updated!")
    }

    override fun isTypingState(p0: IRainbowContact?, state: Boolean, p2: String?) {
        if (state) {
            isOnTypingState = true
            activity_chat_detail_name_status.visibility = View.VISIBLE
            activity_chat_detail_name_status.text = "Typing..."
        } else {
            isOnTypingState = false
            handlePresenceChange(currentAppearance)
        }
    }

    override fun onMoreMessagesListUpdated(p0: Int, p1: String?, p2: MutableList<IMMessage>?) {
        Log.d(LOG_DEBUG_TAG, "More Messages")
    }
}