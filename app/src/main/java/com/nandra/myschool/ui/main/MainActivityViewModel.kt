package com.nandra.myschool.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ale.infra.manager.fileserver.IFileProxy
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.ConnectServerState

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {

    var sentFileStorage = listOf<RainbowFileDescriptor>()
    var receivedFileStorage = listOf<RainbowFileDescriptor>()

    fun updateFileStorage() {
        RainbowSdk.instance().fileStorage().fetchAllFilesReceived(object : IFileProxy.IRefreshListener {
            override fun onRefreshSuccess(list: MutableList<RainbowFileDescriptor>?) {
                Log.d(Utility.LOG_DEBUG_TAG, "FetchReceivedFiles Success")
                receivedFileStorage = list ?: listOf()
            }

            override fun onRefreshFailed() {
                Log.d(Utility.LOG_DEBUG_TAG, "FetchReceivedFiles Failed")
            }
        })

        RainbowSdk.instance().fileStorage().fetchAllFilesSent(object : IFileProxy.IRefreshListener {
            override fun onRefreshSuccess(list: MutableList<RainbowFileDescriptor>?) {
                Log.d(Utility.LOG_DEBUG_TAG, "FetchSentFiles Success")
                sentFileStorage = list ?: listOf()
            }

            override fun onRefreshFailed() {
                Log.d(Utility.LOG_DEBUG_TAG, "FetchSentFiles Failed")
            }
        })
    }

    val connectServerState: LiveData<ConnectServerState>
        get() = _loadingState
    private val _loadingState = MutableLiveData<ConnectServerState>(ConnectServerState.LOADING)
    fun setLoadingState(state: ConnectServerState) {
        _loadingState.postValue(state)
    }

    /*val networkState: LiveData<Utility.NetworkState>
        get() = _networkState
    private val _networkState = MutableLiveData<Utility.NetworkState>(Utility.NetworkState.CONNECTED)
    fun setNetworkState(state: Utility.NetworkState) {
        _networkState.postValue(state)
    }*/
}