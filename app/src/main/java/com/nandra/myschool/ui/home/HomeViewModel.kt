package com.nandra.myschool.ui.home

import androidx.lifecycle.ViewModel
import com.ale.infra.manager.channel.Channel
import com.ale.rainbowsdk.RainbowSdk

class HomeViewModel : ViewModel() {

    fun getAllUserChannel() : List<Channel> {
        return RainbowSdk.instance().channels().allChannels.copyOfDataList
    }

}
