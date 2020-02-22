package com.nandra.myschool.ui.classroom_detail.classroom_feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelItemListAdapter
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.channel_detail_activity.*
import kotlinx.android.synthetic.main.classroom_feed_fragment.*

class ClassroomFeedFragment : Fragment(), IChannelProxy.IChannelFetchItemsListener {

    private lateinit var channel: Channel
    private lateinit var channelItemListAdapter: ChannelItemListAdapter
    private var channelItemList = listOf<ChannelItem>()
    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()
    private val classroomFeedViewModel: ClassroomFeedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomDetailViewModel.detailSubjectDataLoadState.observe(viewLifecycleOwner, Observer {
            handleDetailLoadState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (classroomFeedViewModel.isChannelItemListenerRegistered) {
            channel.channelItems.unregisterChangeListener(this::updateChannelItemList)
        }
        classroomDetailViewModel.changeSubjectDataLoadState(Utility.DataLoadState.UNLOADED)
    }

    override fun onFetchItemsFailed(p0: RainbowServiceException?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFetchItemsSuccess() {
        updateChannelItemList()
    }

    private fun setupView() {
        channelItemListAdapter = ChannelItemListAdapter()
        fragment_classroom_feed_recycler_view.apply {
            adapter = channelItemListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun updateChannelItemList() {
        val newChannelItemList = channel.channelItems.copyOfDataList
        channelItemList = newChannelItemList
        activity?.runOnUiThread {
            channelItemListAdapter.submitList(channelItemList)
            channelItemListAdapter.notifyDataSetChanged()
        }
    }

    private fun setupChannelView() {
        if (!channel.isSubscribed) {
            Log.d(Utility.LOG_DEBUG_TAG, "Not Subscribed")
            RainbowSdk.instance().channels().subscribeToChannel(channel, object : IChannelProxy.IChannelSubscribeListener {
                override fun onSubscribeSuccess(p0: Channel?) {
                    Log.d(Utility.LOG_DEBUG_TAG, "Subscribe Success")
                    RainbowSdk.instance().channels().fetchItems(channel, 20, this@ClassroomFeedFragment)
                    channel.channelItems.registerChangeListener(this@ClassroomFeedFragment::updateChannelItemList)
                    classroomFeedViewModel.isChannelItemListenerRegistered = true
                }

                override fun onSubscribeFailed(p0: RainbowServiceException?) {
                    Log.d(Utility.LOG_DEBUG_TAG, "Subscribe Failed")
                    classroomFeedViewModel.isChannelItemListenerRegistered = false
                    //Show Error Message
                }
            })
        } else {
            Log.d(Utility.LOG_DEBUG_TAG, "Already Subscribed")
            RainbowSdk.instance().channels().fetchItems(channel, 20, this@ClassroomFeedFragment)
            channel.channelItems.registerChangeListener(this::updateChannelItemList)
            classroomFeedViewModel.isChannelItemListenerRegistered = true
        }
    }

    private fun handleDetailLoadState(state: Utility.DataLoadState) {
        when (state) {
            Utility.DataLoadState.UNLOADED -> {
                Log.d(Utility.LOG_DEBUG_TAG, "UnLoaded")
            }
            Utility.DataLoadState.LOADING -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Loading")
            }
            Utility.DataLoadState.LOADED -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Data Loaded")
                channel = RainbowSdk.instance().channels().getChannel(classroomDetailViewModel.detailSubject.channel_id)
                setupChannelView()
            }
            else -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Error")
            }
        }
    }
}