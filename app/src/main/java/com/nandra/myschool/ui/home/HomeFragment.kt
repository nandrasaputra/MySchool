package com.nandra.myschool.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.ChannelItem
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelItemListAdapter
import com.nandra.myschool.ui.main.MainActivityViewModel
import com.nandra.myschool.utils.Utility.ConnectServerState
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var channelItemListAdapter: ChannelItemListAdapter
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val changeListener = IItemListChangeListener(::updateHomeData)
    private var channelItemList = listOf<ChannelItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        RainbowSdk.instance().channels().allChannels.registerChangeListener(changeListener)
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(fragment_home_toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        channelItemListAdapter = ChannelItemListAdapter()
        fragment_home_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = channelItemListAdapter
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.connectServerState.observe(viewLifecycleOwner, Observer {
            handleConnectServerState(it)
        })
        homeViewModel.homeDataLoadState.observe(viewLifecycleOwner, Observer {
            handleDataLoadState(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RainbowSdk.instance().channels().allChannels.unregisterChangeListener(changeListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateHomeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.updateAllUserChannelList()
        }
    }

    private fun setHomeData() {
        channelItemList = homeViewModel.channelItemList
        channelItemListAdapter.submitList(channelItemList)
        channelItemListAdapter.notifyDataSetChanged()
    }

    private fun handleConnectServerState(state: ConnectServerState) {
        when(state) {
            ConnectServerState.LOADING -> { }
            ConnectServerState.SUCCESS -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    homeViewModel.loadInitialData()
                }
            }
            else -> { }
        }
    }

    private fun handleDataLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {

            }
            DataLoadState.LOADED -> {
                setHomeData()
            }
            DataLoadState.LOADING -> {}
            DataLoadState.ERROR -> {}
        }
    }
}
