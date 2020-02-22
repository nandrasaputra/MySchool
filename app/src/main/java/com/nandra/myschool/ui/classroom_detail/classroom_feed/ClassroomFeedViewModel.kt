package com.nandra.myschool.ui.classroom_detail.classroom_feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility

class ClassroomFeedViewModel(app: Application) : AndroidViewModel(app) {

    var isChannelItemListenerRegistered: Boolean = false

}