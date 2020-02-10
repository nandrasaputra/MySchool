package com.nandra.myschool.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nandra.myschool.utils.Utility.ConnectServerState

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {
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