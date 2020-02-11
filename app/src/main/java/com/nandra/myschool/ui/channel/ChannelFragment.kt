package com.nandra.myschool.ui.channel

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelListAdapter
import com.nandra.myschool.ui.MainActivityViewModel
import com.nandra.myschool.utils.Utility.ConnectServerState
import kotlinx.android.synthetic.main.channel_fragment.*

class ChannelFragment : Fragment() {

    private lateinit var channelListAdapter: ChannelListAdapter
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val channelViewModel: ChannelViewModel by activityViewModels()
    private var allChannelList = listOf<Channel>()
    private val changeListener = IItemListChangeListener(::getChannelList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RainbowSdk.instance().channels().allChannels.registerChangeListener(changeListener)
        return inflater.inflate(R.layout.channel_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.connectServerState.observe(viewLifecycleOwner, Observer {
            handleConnectServerState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(fragment_channel_toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        channelListAdapter = ChannelListAdapter()
        fragment_channel_recycler_view.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = channelListAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.channel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RainbowSdk.instance().channels().allChannels.unregisterChangeListener(changeListener)
    }

    private fun getChannelList() {
        allChannelList = channelViewModel.allChannelList.copyOfDataList

        activity?.runOnUiThread {
            channelListAdapter.submitList(allChannelList)
            channelListAdapter.notifyDataSetChanged()
        }
    }

    private fun handleConnectServerState(state: ConnectServerState) {
        when(state) {
            ConnectServerState.SUCCESS -> { getChannelList() }
            ConnectServerState.LOADING -> { }
            else -> {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.channel_all, R.id.channel_owned, R.id.channel_subcribed -> {
                item.isChecked = true
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}