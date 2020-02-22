package com.nandra.myschool.ui.classroom_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.database.*
import com.nandra.myschool.model.Subject
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClassroomDetailViewModel(app: Application) : AndroidViewModel(app) {

    var isChannelItemListenerRegistered: Boolean = false
    var channelFeedItemList: List<ChannelItem> = listOf()
    var currentChannel: Channel? = null
    private val channelFetchItemListener = object : IChannelProxy.IChannelFetchItemsListener {
        override fun onFetchItemsFailed(p0: RainbowServiceException?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onFetchItemsSuccess() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val channelSubscriberListener = object : IChannelProxy.IChannelSubscribeListener {
        override fun onSubscribeSuccess(p0: Channel?) {
            Log.d(Utility.LOG_DEBUG_TAG, "Subscribe Success")
            RainbowSdk.instance().channels().fetchItems(currentChannel, 20, channelFetchItemListener)
            currentChannel!!.channelItems.registerChangeListener(::updateChannelListItem)
            isChannelItemListenerRegistered = true
        }

        override fun onSubscribeFailed(p0: RainbowServiceException?) {
            Log.d(Utility.LOG_DEBUG_TAG, "Subscribe Failed")
            isChannelItemListenerRegistered = false
            //Show Error Message
        }
    }


    private var fetchDetailSubjectDatabaseReferenceJob: Job? = null
    private val repository = MySchoolRepository()
    val detailSubjectDataLoadState: LiveData<Utility.DataLoadState>
        get() = _detailSubjectDataLoadState
    private val _detailSubjectDataLoadState = MutableLiveData<Utility.DataLoadState>(Utility.DataLoadState.UNLOADED)
    private var detailSubjectQuery: Query? = null
    var detailSubject = Subject()

    fun getDetailSubject(subjectID: Int) {
        //HandleInternetConnectionHere
        viewModelScope.launch(Dispatchers.IO) {
            fetchDetailSubject(subjectID)
        }
    }

    fun changeSubjectDataLoadState(state: Utility.DataLoadState) {
        _detailSubjectDataLoadState.value = state
    }

    private suspend fun fetchDetailSubject(subjectID: Int) {
        fetchDetailSubjectDatabaseReferenceJob?.run {
            this.join()
        }
        _detailSubjectDataLoadState.postValue(Utility.DataLoadState.LOADING)
        detailSubjectQuery = repository.getThirdGradeSubjectById(subjectID)
        detailSubjectQuery?.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) { _detailSubjectDataLoadState.postValue(Utility.DataLoadState.ERROR) }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                detailSubject = dataSnapshot.getValue(Subject::class.java) ?: Subject()
                _detailSubjectDataLoadState.postValue(Utility.DataLoadState.LOADED)
            }
        })
    }

    private fun updateChannelListItem() {

    }

    fun fetchChannelFeedItemById(id: String) {
        currentChannel = RainbowSdk.instance().channels().getChannel(id)
        if (currentChannel!!.isSubscribed) {
            RainbowSdk.instance().channels().subscribeToChannel(currentChannel, channelSubscriberListener)
        } else {

        }
    }

}