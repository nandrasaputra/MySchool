package com.nandra.myschool.ui.classroom_detail.classroom_feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelItemListAdapter
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.classroom_feed_fragment.*

class ClassroomFeedFragment : Fragment() {

    private lateinit var channelItemListAdapter: ChannelItemListAdapter
    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()

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
        classroomDetailViewModel.unregisterAnyChannelFetchItemListener()
        classroomDetailViewModel.changeSubjectDataLoadState(DataLoadState.UNLOADED)
    }

    private fun setupView() {
        channelItemListAdapter = ChannelItemListAdapter(::onFeedItemFailureCallback)
        fragment_classroom_feed_recycler_view.apply {
            adapter = channelItemListAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
    }

    private fun handleDetailLoadState(state: DataLoadState) {
        when (state) {
            DataLoadState.UNLOADED -> { }
            DataLoadState.LOADING -> {
                fragment_classroom_feed_shimmer_veil.visibility = View.VISIBLE
                fragment_classroom_feed_shimmer_layout.visibility = View.VISIBLE
                fragment_classroom_feed_shimmer_layout.startShimmer()
            }
            DataLoadState.LOADED -> {
                channelItemListAdapter.submitList(classroomDetailViewModel.channelFeedItemList)
                channelItemListAdapter.notifyDataSetChanged()
                fragment_classroom_feed_shimmer_veil.visibility = View.GONE
                fragment_classroom_feed_shimmer_layout.visibility = View.GONE
                fragment_classroom_feed_shimmer_layout.stopShimmer()
            }
            else -> { }
        }
    }

    private fun onFeedItemFailureCallback() {
        classroomDetailViewModel.refreshChannelItemList()
    }
}