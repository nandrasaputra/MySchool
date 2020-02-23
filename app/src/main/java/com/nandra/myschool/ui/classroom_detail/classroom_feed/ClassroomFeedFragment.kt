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
import kotlinx.android.synthetic.main.classroom_feed_fragment.*

class ClassroomFeedFragment : Fragment() {

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
        classroomDetailViewModel.channelFeedItemList.observe(viewLifecycleOwner, Observer {
            if (classroomDetailViewModel.detailSubjectDataLoadState.value == Utility.DataLoadState.LOADED) {
                channelItemListAdapter.submitList(it)
                channelItemListAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        classroomDetailViewModel.unregisterAnyChannelFetchItemListener()
        classroomDetailViewModel.changeSubjectDataLoadState(Utility.DataLoadState.UNLOADED)
    }

    private fun setupView() {
        channelItemListAdapter = ChannelItemListAdapter(::onFeedItemFailureCallback)
        fragment_classroom_feed_recycler_view.apply {
            adapter = channelItemListAdapter
            layoutManager = LinearLayoutManager(activity)
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
                Log.d(Utility.LOG_DEBUG_TAG, "Loaded")
            }
            else -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Error")
            }
        }
    }

    private fun onFeedItemFailureCallback() {
        classroomDetailViewModel.refreshChannelItemList()
    }
}