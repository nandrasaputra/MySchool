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

    val connectServerState: LiveData<ConnectServerState>
        get() = _loadingState
    private val _loadingState = MutableLiveData<ConnectServerState>(ConnectServerState.LOADING)
    fun setLoadingState(state: ConnectServerState) {
        _loadingState.postValue(state)
    }
}