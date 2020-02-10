package com.nandra.myschool.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.infra.manager.channel.ChannelItem
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(val app: Application) : AndroidViewModel(app) {

    var channelItemList = listOf<ChannelItem>()
    private var homeFeedJob: Job? = null
    private var isInitialDataLoaded: Boolean = false

    val homeDataLoadState : LiveData<DataLoadState>
        get() = _homeLoadingState
    private val _homeLoadingState = MutableLiveData<DataLoadState>(DataLoadState.UNLOADED)

    suspend fun updateAllUserChannelList() {
        if (homeFeedJob != null) {
            homeFeedJob?.join()
        }
        homeFeedJob = viewModelScope.launch {
            _homeLoadingState.postValue(DataLoadState.LOADING)
            fetchChannelsFeed()
            _homeLoadingState.postValue(DataLoadState.LOADED)
        }
    }

    suspend fun loadInitialData() {
        if(!isInitialDataLoaded) {
            updateAllUserChannelList()
            isInitialDataLoaded = true
        }
    }

    private fun fetchChannelsFeed() {
        val allChannels = RainbowSdk.instance().channels().allChannels.items
        val channelItem = mutableListOf<ChannelItem>()
        for (channel in allChannels) {
            channelItem.addAll(channel.channelItems.items)
        }
        channelItem.sortByDescending {
            it.date
        }
        channelItemList = channelItem
    }
}
