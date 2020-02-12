package com.nandra.myschool.ui.channel

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ChannelListAdapter
import com.nandra.myschool.ui.MainActivityViewModel
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.ConnectServerState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.channel_fragment.*

class ChannelFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var channelListAdapter: ChannelListAdapter
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val channelViewModel: ChannelViewModel by activityViewModels()
    private var channelList = listOf<Channel>()
    private val changeListener = IItemListChangeListener(::getChannelList)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentChannelPreferenceValue: String

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

        setupSharedPreferences()

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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun getChannelList() {
        channelList = when (currentChannelPreferenceValue) {
            Utility.CHANNEL_ALL_CATEGORY_VALUE -> { channelViewModel.getAllChannelList() }
            Utility.CHANNEL_SUBSCRIBED_CATEGORY_VALUE -> { channelViewModel.getSubscribedChannelList() }
            Utility.CHANNEL_OWNED_CATEGORY_VALUE -> { channelViewModel.getOwnedChannelList() }
            else -> throw IllegalArgumentException("Wrong Argument, Please Check Channel Preference Value!")
        }

        activity?.runOnUiThread {
            channelListAdapter.submitList(channelList)
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

    private fun setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        currentChannelPreferenceValue = sharedPreferences.getString(Utility.CHANNEL_CATEGORY_PREFERENCE_KEY, Utility.CHANNEL_ALL_CATEGORY_VALUE)
            ?: Utility.CHANNEL_ALL_CATEGORY_VALUE
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        when (currentChannelPreferenceValue) {
            Utility.CHANNEL_ALL_CATEGORY_VALUE -> { menu.findItem(R.id.channel_all).isChecked = true }
            Utility.CHANNEL_SUBSCRIBED_CATEGORY_VALUE -> { menu.findItem(R.id.channel_subcribed).isChecked = true }
            Utility.CHANNEL_OWNED_CATEGORY_VALUE -> { menu.findItem(R.id.channel_owned).isChecked = true }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.channel_all -> {
                changeChannelPreferenceValue(Utility.CHANNEL_ALL_CATEGORY_VALUE)
                item.isChecked = true
                true
            }
            R.id.channel_subcribed -> {
                changeChannelPreferenceValue(Utility.CHANNEL_SUBSCRIBED_CATEGORY_VALUE)
                item.isChecked = true
                true
            }
            R.id.channel_owned -> {
                changeChannelPreferenceValue(Utility.CHANNEL_OWNED_CATEGORY_VALUE)
                item.isChecked = true
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Utility.CHANNEL_CATEGORY_PREFERENCE_KEY -> { getChannelList() }
        }
    }

    private fun changeChannelPreferenceValue(value: String) {
        if (value != currentChannelPreferenceValue) {
            currentChannelPreferenceValue = value
            with(sharedPreferences.edit()) {
                putString(Utility.CHANNEL_CATEGORY_PREFERENCE_KEY, value)
                commit()
            }
        }
    }


}