package com.nandra.myschool.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nandra.myschool.utils.Utility.LoadingState

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {
    val loadingState: LiveData<LoadingState>
        get() = _loadingState
    private val _loadingState = MutableLiveData<LoadingState>(LoadingState.LOADING)
    fun setLoadingState(state: LoadingState) {
        _loadingState.postValue(state)
    }

    /*val networkState: LiveData<Utility.NetworkState>
        get() = _networkState
    private val _networkState = MutableLiveData<Utility.NetworkState>(Utility.NetworkState.CONNECTED)
    fun setNetworkState(state: Utility.NetworkState) {
        _networkState.postValue(state)
    }*/
}