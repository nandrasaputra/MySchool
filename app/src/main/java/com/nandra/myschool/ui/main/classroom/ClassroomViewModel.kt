package com.nandra.myschool.ui.main.classroom

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.database.*
import com.nandra.myschool.model.Schedule
import com.nandra.myschool.model.Subject
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClassroomViewModel(app: Application) : AndroidViewModel(app) {

    private var fetchSubjectDatabaseReferenceJob: Job? = null
    private var fetchScheduleDatabaseReferenceJob: Job? = null
    private val repository = MySchoolRepository()

    val subjectDataLoadState: LiveData<DataLoadState>
        get() = _subjectDataLoadState
    private val _subjectDataLoadState = MutableLiveData<DataLoadState>(DataLoadState.UNLOADED)

    val scheduleDataLoadState: LiveData<DataLoadState>
        get() = _scheduleDataLoadState
    private val _scheduleDataLoadState = MutableLiveData<DataLoadState>(DataLoadState.UNLOADED)

    private var subjectDatabaseReference: DatabaseReference? = null
    var subjectList = listOf<Subject>()

    private var scheduleDatabaseReference: DatabaseReference? = null
    var scheduleList = listOf<Schedule>()

    fun getSubjectList() {
        //HandleInternetConnectionHere
        viewModelScope.launch(Dispatchers.IO) {
            fetchSubjectList()
        }
    }

    fun getScheduleList() {
        //HandleInternetConnectionHere
        viewModelScope.launch(Dispatchers.IO) {
            fetchScheduleList()
        }
    }

    private suspend fun fetchScheduleList() {
        fetchScheduleDatabaseReferenceJob?.run {
            this.join()
        }
        _scheduleDataLoadState.postValue(DataLoadState.LOADING)
        scheduleDatabaseReference = repository.getScheduleListByUserId(RainbowSdk.instance().myProfile().connectedUser.id)
        scheduleDatabaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _scheduleDataLoadState.postValue(DataLoadState.ERROR)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newScheduleList = mutableListOf<Schedule>()
                for (item in dataSnapshotList) {
                    newScheduleList.add(item.getValue(Schedule::class.java)!!)
                }
                Log.d(LOG_DEBUG_TAG, newScheduleList.size.toString())
                scheduleList = newScheduleList
                _scheduleDataLoadState.postValue(DataLoadState.LOADED)
            }
        })
    }

    private suspend fun fetchSubjectList() {
        fetchSubjectDatabaseReferenceJob?.run {
            this.join()
        }
        _subjectDataLoadState.postValue(DataLoadState.LOADING)
        subjectDatabaseReference = repository.getSubjectByUserId(RainbowSdk.instance().myProfile().connectedUser.id)
        subjectDatabaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                _subjectDataLoadState.postValue(DataLoadState.ERROR)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newSubjectList = mutableListOf<Subject>()
                for (item in dataSnapshotList) {
                    newSubjectList.add(item.getValue(Subject::class.java)!!)
                }
                subjectList = newSubjectList
                _subjectDataLoadState.postValue(DataLoadState.LOADED)
            }
        })
    }
}