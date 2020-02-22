package com.nandra.myschool.ui.main.classroom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.nandra.myschool.model.Subject
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClassroomViewModel(app: Application) : AndroidViewModel(app) {

    private var fetchSubjectDatabaseReferenceJob: Job? = null
    private val repository = MySchoolRepository()
    val subjectDataLoadState: LiveData<DataLoadState>
        get() = _subjectDataLoadState
    private val _subjectDataLoadState = MutableLiveData<DataLoadState>(DataLoadState.UNLOADED)
    private var subjectDatabaseReference: DatabaseReference? = null
    var subjectList = listOf<Subject>()

    fun getSubjectList() {
        //HandleInternetConnectionHere
        viewModelScope.launch(Dispatchers.IO) {
            fetchSubjectList()
        }
    }

    private suspend fun fetchSubjectList() {
        fetchSubjectDatabaseReferenceJob?.run {
            this.join()
        }
        _subjectDataLoadState.postValue(DataLoadState.LOADING)
        subjectDatabaseReference = repository.getThirdGradeSubjectListReference()
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