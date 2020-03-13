package com.nandra.myschool.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility.ConnectServerState

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {

    fun checkServerLoadingState() {
        if (RainbowSdk.instance().connection().isInProgress) {
            _loadingState.value = ConnectServerState.LOADING
        } else if (RainbowSdk.instance().connection().isConnected) {
            _loadingState.value = ConnectServerState.SUCCESS
        }
    }

    val connectServerState: LiveData<ConnectServerState>
        get() = _loadingState
    private val _loadingState = MutableLiveData(ConnectServerState.LOADING)
    fun setLoadingState(state: ConnectServerState) {
        _loadingState.postValue(state)
    }
}