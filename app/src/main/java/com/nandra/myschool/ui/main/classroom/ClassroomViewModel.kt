package com.nandra.myschool.ui.main.classroom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.nandra.myschool.model.Schedule
import com.nandra.myschool.model.Subject
import com.nandra.myschool.model.User
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClassroomViewModel(app: Application) : AndroidViewModel(app) {

    var currentUser = User()
    private var fetchSubjectDatabaseReferenceJob: Job? = null
    private var fetchScheduleDatabaseReferenceJob: Job? = null
    private var fetchUserDatabaseReferenceJob: Job? = null
    private val repository = MySchoolRepository()

    val userLoadState: LiveData<DataLoadState>
        get() = _userDataLoadState
    private val _userDataLoadState = MutableLiveData(DataLoadState.UNLOADED)

    val subjectDataLoadState: LiveData<DataLoadState>
        get() = _subjectDataLoadState
    private val _subjectDataLoadState = MutableLiveData(DataLoadState.UNLOADED)

    val scheduleDataLoadState: LiveData<DataLoadState>
        get() = _scheduleDataLoadState
    private val _scheduleDataLoadState = MutableLiveData(DataLoadState.UNLOADED)

    private var subjectDatabaseReference: DatabaseReference? = null
    var subjectList = listOf<Subject>()

    private var scheduleDatabaseReference: DatabaseReference? = null
    var scheduleList = listOf<Schedule>()

    fun getSubjectList() {
        //HandleInternetConnectionHere
        viewModelScope.launch {
            fetchSubjectList()
        }
    }

    fun getScheduleList() {
        //HandleInternetConnectionHere
        viewModelScope.launch {
            fetchScheduleList()
        }
    }

    fun getUser() {
        //HandleInternetConnectionHere
        viewModelScope.launch {
            fetchUser()
        }
    }

    private suspend fun fetchUser() {
        fetchUserDatabaseReferenceJob?.run {
            this.join()
        }
        _userDataLoadState.value = DataLoadState.LOADING
        val userDatabaseReference = repository.getUserDatabaseReference(RainbowSdk.instance().myProfile().connectedUser.id)
        userDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                _userDataLoadState.value = DataLoadState.ERROR
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                currentUser = dataSnapShot.getValue(User::class.java)!!
                _userDataLoadState.value = DataLoadState.LOADED
            }

        })
    }

    private suspend fun fetchScheduleList() {
        fetchScheduleDatabaseReferenceJob?.run {
            this.join()
        }
        _scheduleDataLoadState.value = DataLoadState.LOADING
        scheduleDatabaseReference = repository.getScheduleDatabaseReference(RainbowSdk.instance().myProfile().connectedUser.id)
        scheduleDatabaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _scheduleDataLoadState.value = DataLoadState.ERROR
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newScheduleList = mutableListOf<Schedule>()
                for (item in dataSnapshotList) {
                    newScheduleList.add(item.getValue(Schedule::class.java)!!)
                }
                scheduleList = newScheduleList
                _scheduleDataLoadState.value = DataLoadState.LOADED
            }
        })
    }

    private suspend fun fetchSubjectList() {
        fetchSubjectDatabaseReferenceJob?.run {
            this.join()
        }
        _subjectDataLoadState.value = DataLoadState.LOADING
        subjectDatabaseReference = repository.getSubjectDatabaseReference(RainbowSdk.instance().myProfile().connectedUser.id)
        subjectDatabaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _subjectDataLoadState.value = DataLoadState.ERROR
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newSubjectList = mutableListOf<Subject>()
                for (item in dataSnapshotList) {
                    newSubjectList.add(item.getValue(Subject::class.java)!!)
                }
                subjectList = newSubjectList
                _subjectDataLoadState.value = DataLoadState.LOADED
            }
        })
    }
}