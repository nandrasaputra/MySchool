package com.nandra.myschool.ui.classroom_detail

import android.app.Application
import android.net.Uri
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
import com.nandra.myschool.utils.Utility.getCurrentStringDate
import com.nandra.myschool.utils.Utility.nameBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClassroomDetailViewModel(app: Application) : AndroidViewModel(app) {

    var userRole = ""
    var subjectCode = ""
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var fetchMaterialDatabaseReferenceJob: Job? = null
    val materialDataLoadState: LiveData<DataLoadState>
        get() = _materialDataLoadState
    private val _materialDataLoadState = MutableLiveData(DataLoadState.UNLOADED)
    private var materialDatabaseReference: DatabaseReference? = null
    var materialList = listOf<Material>()

    private var fetchSessionDatabaseReferenceJob: Job? = null
    val sessionDataLoadState: LiveData<DataLoadState>
        get() = _sessionDataLoadState
    private val _sessionDataLoadState = MutableLiveData(DataLoadState.UNLOADED)
    private var sessionDatabaseQuery: Query? = null
    var sessionList = listOf<Session>()

    val uploadFileState: LiveData<UploadFileState>
        get() = _uploadFileState
    private val _uploadFileState = MutableLiveData(UploadFileState.IDLE)
    private var currentUploadTask: UploadTask? = null


    private var fetchDetailSubjectDatabaseReferenceJob: Job? = null
    private val repository = MySchoolRepository()
    val detailSubjectDataLoadState: LiveData<DataLoadState>
        get() = _detailSubjectDataLoadState
    private val _detailSubjectDataLoadState = MutableLiveData(DataLoadState.UNLOADED)
    private var detailSubjectQuery: Query? = null
    var detailSubject = Subject()

    var isChannelItemListenerRegistered: Boolean = false

    var classroomFeedItemList = listOf<ChannelItem>()

    var currentChannel: Channel? = null
    private val channelFetchItemListener = object : IChannelProxy.IChannelFetchItemsListener {
        override fun onFetchItemsFailed(p0: RainbowServiceException?) { }

        override fun onFetchItemsSuccess() {
            updateChannelListItem()
        }
    }

    fun getSessionList() {
        //HandleInternetConnectionHere
        viewModelScope.launch {
            fetchSessionList()
        }
    }

    private suspend fun fetchSessionList() {
        fetchSessionDatabaseReferenceJob?.run {
            this.join()
        }
        _sessionDataLoadState.value = DataLoadState.LOADING
        sessionDatabaseQuery = repository.getThirdGradeSessionQuery(subjectCode)
        sessionDatabaseQuery?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _sessionDataLoadState.value = DataLoadState.ERROR
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newSessionList = mutableListOf<Session>()
                for (item in dataSnapshotList) {
                    newSessionList.add(item.getValue(Session::class.java)!!)
                }
                sessionList = newSessionList.reversed()
                _sessionDataLoadState.value = DataLoadState.LOADED
            }
        })
    }

    fun getMaterialList() {
        //HandleInternetConnectionHere
        viewModelScope.launch {
            fetchMaterialList()
        }
    }

    private suspend fun fetchMaterialList() {
        fetchMaterialDatabaseReferenceJob?.run {
            this.join()
        }
        _materialDataLoadState.value = DataLoadState.LOADING
        materialDatabaseReference = repository.getMaterialDatabaseReference(subjectCode)
        materialDatabaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _materialDataLoadState.value = DataLoadState.ERROR
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newMaterialList = mutableListOf<Material>()
                for (item in dataSnapshotList) {
                    newMaterialList.add(item.getValue(Material::class.java)!!)
                }
                materialList = newMaterialList.reversed()
                _materialDataLoadState.value = DataLoadState.LOADED
            }
        })
    }

    private val channelSubscriberListener = object : IChannelProxy.IChannelSubscribeListener {
        override fun onSubscribeSuccess(p0: Channel?) {
            RainbowSdk.instance().channels().fetchItems(currentChannel, 20, channelFetchItemListener)
            currentChannel!!.channelItems.registerChangeListener(::updateChannelListItem)
            isChannelItemListenerRegistered = true
        }

        override fun onSubscribeFailed(p0: RainbowServiceException?) {
            _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
            //Show Error Message
        }
    }

    fun getDetailSubject(subjectID: Int) {
        //HandleInternetConnectionHere
        viewModelScope.launch {
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
        classroomFeedItemList = newChannelItemList
        _detailSubjectDataLoadState.postValue(DataLoadState.LOADED)
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
                        _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
                    }
                } else {
                    _detailSubjectDataLoadState.postValue(DataLoadState.ERROR)
                }
            }
        } else {
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

    fun uploadFileToFirebase(uri: Uri, materialName: String, mimeType: String, fileExtension: String) {
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
                    val material = Material(materialName, mimeType, getCurrentStringDate(), it.toString())
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
        val channelItem = ChannelItemBuilder().setMessage(message).build()
        RainbowSdk.instance().channels().createItem(currentChannel, channelItem, object : IChannelProxy.IChannelCreateItemListener {
            override fun onCreateMessageItemFailed(p0: RainbowServiceException?) { }

            override fun onCreateMessageItemSuccess(p0: String?) {
                refreshChannelItemList()
            }
        })
    }

    fun addNewSession(topic: String, description: String) {
        val date = getCurrentStringDate()
        val initiatorName = nameBuilder(RainbowSdk.instance().myProfile().connectedUser)
        val key = database.reference.child("session").child("third_grade").child(subjectCode).push().key
        val path = "/session/third_grade/$subjectCode/$key"
        val session = Session(topic, description, initiatorName, date, "Open", key!!, subjectCode, grade = "third_grade")

        val childUpdate = HashMap<String, Any>()
        childUpdate[path] = session
        database.reference.updateChildren(childUpdate)
    }

}