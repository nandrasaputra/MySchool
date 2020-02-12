package com.nandra.myschool.ui.channel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ale.infra.manager.channel.Channel
import com.ale.rainbowsdk.RainbowSdk

class ChannelViewModel(app: Application) : AndroidViewModel(app) {
    fun getAllChannelList() : List<Channel> = RainbowSdk.instance().channels().allChannels.copyOfDataList
    fun getSubscribedChannelList() : List<Channel> = RainbowSdk.instance().channels().allSubscribedChannels
    fun getOwnedChannelList() : List<Channel> = RainbowSdk.instance().channels().allOwnedChannels
}