package com.nandra.myschool.ui.classroom_detail

import android.app.Application
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.nandra.myschool.model.Material
import com.nandra.myschool.model.Subject
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.UploadFileState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClassroomDetailViewModel(app: Application) : AndroidViewModel(app) {

    val uploadFileState: LiveData<UploadFileState>
        get() = _uploadFileState
    private val _uploadFileState = MutableLiveData<UploadFileState>(UploadFileState.IDLE)
    private var currentUploadTask: UploadTask? = null

    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private var fetchDetailSubjectDatabaseReferenceJob: Job? = null
    private val repository = MySchoolRepository()
    val detailSubjectDataLoadState: LiveData<DataLoadState>
        get() = _detailSubjectDataLoadState
    private val _detailSubjectDataLoadState = MutableLiveData<DataLoadState>(DataLoadState.UNLOADED)
    private var detailSubjectQuery: Query? = null
    var detailSubject = Subject()

    var isChannelItemListenerRegistered: Boolean = false

    val channelFeedItemList: LiveData<List<ChannelItem>>
        get() = _channelFeedItemList
    private val _channelFeedItemList = MutableLiveData<List<ChannelItem>>(listOf())

    var currentChannel: Channel? = null
    private val channelFetchItemListener = object : IChannelProxy.IChannelFetchItemsListener {
        override fun onFetchItemsFailed(p0: RainbowServiceException?) {
            throw Exception("LOLZ")
        }

        override fun onFetchItemsSuccess() {
            updateChannelListItem()
        }
    }

    private val channelSubscriberListener = object : IChannelProxy.IChannelSubscribeListener {
        override fun onSubscribeSuccess(p0: Channel?) {
            Log.d(LOG_DEBUG_TAG, "Subscribe Success")
            RainbowSdk.instance().channels().fetchItems(currentChannel, 20, channelFetchItemListener)
            currentChannel!!.channelItems.registerChangeListener(::updateChannelListItem)
            isChannelItemListenerRegistered = true
            _detailSubjectDataLoadState.postValue(DataLoadState.LOADED)
        }

        override fun onSubscribeFailed(p0: RainbowServiceException?) {
            Log.d(LOG_DEBUG_TAG, "Subscribe Failed")
            _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
            //Show Error Message
        }
    }

    fun getDetailSubject(subjectID: Int) {
        //HandleInternetConnectionHere
        viewModelScope.launch(Dispatchers.IO) {
            fetchDetailSubject(subjectID)
        }
    }

    fun changeSubjectDataLoadState(state: DataLoadState) {
        _detailSubjectDataLoadState.value = state
    }

    private suspend fun fetchDetailSubject(subjectID: Int) {
        fetchDetailSubjectDatabaseReferenceJob?.run {
            this.join()
        }
        _detailSubjectDataLoadState.postValue(DataLoadState.LOADING)
        detailSubjectQuery = repository.getThirdGradeSubjectById(subjectID)
        detailSubjectQuery?.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(LOG_DEBUG_TAG, "Firebase Database OnCanceled")
                _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val newDetailSubject = dataSnapshot.getValue(Subject::class.java)
                if (newDetailSubject != null) {
                    detailSubject = newDetailSubject
                    fetchChannelById(detailSubject.channel_id)
                } else {
                    //show error and retry
                    Log.d(LOG_DEBUG_TAG, "subject is null")
                    _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
                }
            }
        })
    }

    private fun updateChannelListItem() {
        val newChannelItemList = currentChannel!!.channelItems.copyOfDataList
        newChannelItemList.sortByDescending {
            it.date
        }
        _channelFeedItemList.postValue(newChannelItemList)
    }

    private fun retryFetchChannel(id: String) {
        if (true /*Internet Available*/) {
            viewModelScope.launch {
                delay(500L)
                if (RainbowSdk.instance().channels() != null) {
                    currentChannel = RainbowSdk.instance().channels().getChannel(id)
                    if (currentChannel != null) {
                        fetchChannelItem()
                    } else {
                        Log.d(LOG_DEBUG_TAG, "currentChannel is null")
                        _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
                    }
                } else {
                    Log.d(LOG_DEBUG_TAG, "null channel")
                    _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
                }
            }
        } else {
            Log.d(LOG_DEBUG_TAG, "Internet Not Available")
            _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
        }
    }

    private fun fetchChannelById(id: String) {
        if (RainbowSdk.instance().channels() != null) {
            currentChannel = RainbowSdk.instance().channels().getChannel(id)
            if (currentChannel != null) {
                fetchChannelItem()
            } else {
                retryFetchChannel(id)
            }
        } else {
            retryFetchChannel(id)
        }
    }

    private fun fetchChannelItem() {
        if (currentChannel!!.isSubscribed) {
            RainbowSdk.instance().channels().fetchItems(currentChannel, 20, channelFetchItemListener)
            currentChannel!!.channelItems.registerChangeListener(this::updateChannelListItem)
            isChannelItemListenerRegistered = true
            _detailSubjectDataLoadState.postValue(DataLoadState.LOADED)
        } else {
            RainbowSdk.instance().channels().subscribeToChannel(currentChannel, channelSubscriberListener)
        }
    }

    fun unregisterAnyChannelFetchItemListener() {
        if (isChannelItemListenerRegistered) {
            currentChannel?.channelItems?.unregisterChangeListener(this::updateChannelListItem)
            isChannelItemListenerRegistered = false
        }
    }

    fun refreshChannelItemList() {
        updateChannelListItem()
    }

    fun uploadFileToFirebase(uri: Uri, fileExtension: String) {
        val subjectCode = detailSubject.subject_code
        val fileName: String = System.currentTimeMillis().toString() + "." + fileExtension
        val subjectRef = storage.reference.child("material").child("third_grade").child(subjectCode)
        val fullRef = subjectRef.child(fileName)

        currentUploadTask = fullRef.putFile(uri)
        _uploadFileState.value = UploadFileState.UPLOADING
        currentUploadTask?.run {
            this.addOnFailureListener {
                _uploadFileState.value = UploadFileState.FAILED
            }.addOnSuccessListener {
                fullRef.downloadUrl.addOnSuccessListener {
                    val databaseRef = database.reference.child("material").child("third_grade").child(subjectCode)
                    val material = Material("lala", "la", "la", "la", it.toString())
                    postMaterial(material, databaseRef)
                }.addOnFailureListener {
                    _uploadFileState.value = UploadFileState.FAILED
                }
            }
        }
    }

    fun resetUploadFileState() {
        _uploadFileState.value = UploadFileState.IDLE
        currentUploadTask = null
    }

    fun attemptToCancelUpload() {
        currentUploadTask?.run{
            this.cancel()
        }
    }

    fun isUploadInProgress() : Boolean {
        return currentUploadTask?.isInProgress ?: false
    }

    private fun postMaterial(material: Material, reference: DatabaseReference) {
        reference.push().setValue(material).addOnCompleteListener {
            _uploadFileState.value = UploadFileState.UPLOADED
        }.addOnFailureListener {
            _uploadFileState.value = UploadFileState.FAILED
        }
    }

}