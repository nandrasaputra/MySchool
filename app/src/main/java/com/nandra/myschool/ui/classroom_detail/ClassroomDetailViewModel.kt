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
import com.ale.infra.manager.channel.ChannelItemBuilder
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.nandra.myschool.model.Material
import com.nandra.myschool.model.Session
import com.nandra.myschool.model.Subject
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.UploadFileState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.getCurrentStringDate
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ClassroomDetailViewModel(app: Application) : AndroidViewModel(app) {

    var userRole = ""

    private var fetchSessionDatabaseReferenceJob: Job? = null
    val sessionDataLoadState: LiveData<DataLoadState>
        get() = _sessionDataLoadState
    private val _sessionDataLoadState = MutableLiveData<DataLoadState>(DataLoadState.UNLOADED)
    private var sessionDatabaseQuery: Query? = null
    var sessionList = listOf<Session>()

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

    fun getSessionList() {
        //HandleInternetConnectionHere
        viewModelScope.launch(Dispatchers.IO) {
            fetchSessionList()
        }
    }

    private suspend fun fetchSessionList() {
        val subjectCode = detailSubject.subject_code
        fetchSessionDatabaseReferenceJob?.run {
            this.join()
        }
        _sessionDataLoadState.postValue(DataLoadState.LOADING)
        sessionDatabaseQuery = repository.getThirdGradeSessionQuery(subjectCode)
        sessionDatabaseQuery?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _sessionDataLoadState.postValue(DataLoadState.ERROR)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newSessionList = mutableListOf<Session>()
                for (item in dataSnapshotList) {
                    newSessionList.add(item.getValue(Session::class.java)!!)
                }
                sessionList = newSessionList.reversed()
                _sessionDataLoadState.postValue(DataLoadState.LOADED)
            }
        })
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
        detailSubjectQuery = repository.getThirdGradeSubjectQuery(subjectID)
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

    fun addNewItemToChannel(message: String) {
        Log.d(LOG_DEBUG_TAG, "Send Button Clicked, Message = $message")
        val channelItem = ChannelItemBuilder().setMessage(message).build()
        RainbowSdk.instance().channels().createItem(currentChannel, channelItem, object : IChannelProxy.IChannelCreateItemListener {
            override fun onCreateMessageItemFailed(p0: RainbowServiceException?) {
                Log.d(LOG_DEBUG_TAG, "Create Item Failed")
            }

            override fun onCreateMessageItemSuccess(p0: String?) {
                refreshChannelItemList()
                Log.d(LOG_DEBUG_TAG, "Create Item Success")
            }
        })
    }

    fun addNewSession(topic: String, description: String) {
        val subjectCode = detailSubject.subject_code
        val date = getCurrentStringDate()
        val initiatorName = nameBuilder(RainbowSdk.instance().myProfile().connectedUser)
        val key = database.reference.child("session").child("third_grade").child(subjectCode).push().key
        val path = "/session/third_grade/$subjectCode/$key"
        val session = Session(topic, description, initiatorName, date, "Open", key!!, subjectCode)

        val childUpdate = HashMap<String, Any>()
        childUpdate[path] = session
        database.reference.updateChildren(childUpdate).addOnSuccessListener {
            Log.d(LOG_DEBUG_TAG, "SESSION POST SUCCESS")
        }.addOnFailureListener {
            Log.d(LOG_DEBUG_TAG, "SESSION POST FAILED")
        }
    }

}