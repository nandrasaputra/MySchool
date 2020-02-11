package com.nandra.myschool.ui.channel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ale.rainbowsdk.RainbowSdk

class ChannelViewModel(app: Application) : AndroidViewModel(app) {
    val allChannelList = RainbowSdk.instance().channels().allChannels
    val subscribedChannelList = RainbowSdk.instance().channels().allSubscribedChannels
    val ownedChannelList = RainbowSdk.instance().channels().allOwnedChannels
}