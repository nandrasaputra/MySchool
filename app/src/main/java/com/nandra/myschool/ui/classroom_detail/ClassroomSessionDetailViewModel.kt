package com.nandra.myschool.ui.classroom_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.database.*
import com.nandra.myschool.model.SessionAttendance
import com.nandra.myschool.model.User
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.getCurrentStringDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClassroomSessionDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MySchoolRepository()
    private var fetchAttendanceDatabaseReferenceJob: Job? = null

    private var attendanceDatabaseReference: DatabaseReference? = null
    var attendanceList = listOf<SessionAttendance>()

    val attendanceLoadState: LiveData<DataLoadState>
        get() = _attendanceLoadState
    private val _attendanceLoadState = MutableLiveData(DataLoadState.UNLOADED)

    fun getAttendanceList(subjectCode: String, sessionKey: String) {
        //handle internet connection here
        viewModelScope.launch {
            fetchAttendanceList(subjectCode, sessionKey)
        }
    }

    private suspend fun fetchAttendanceList(subjectCode: String, sessionKey: String) {
        fetchAttendanceDatabaseReferenceJob?.run {
            this.join()
        }
        _attendanceLoadState.value = DataLoadState.LOADING
        attendanceDatabaseReference = repository.getAttendanceDatabaseReference(subjectCode, sessionKey)
        attendanceDatabaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                _attendanceLoadState.value = DataLoadState.ERROR
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshotList = dataSnapshot.children
                val newSessionAttendanceList = mutableListOf<SessionAttendance>()
                for (item in dataSnapshotList) {
                    newSessionAttendanceList.add(item.getValue(SessionAttendance::class.java)!!)
                }
                attendanceList = newSessionAttendanceList
                _attendanceLoadState.value = DataLoadState.LOADED
            }
        })
    }

    fun submitAttendance(subjectCode: String, sessionKey: String) {

        val userId = RainbowSdk.instance().myProfile().connectedUser.id ?: ""
        repository.getUserDatabaseReference(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(LOG_DEBUG_TAG, "Submit Attendance Failed")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = Utility.nameBuilder(RainbowSdk.instance().myProfile().connectedUser)
                val date = getCurrentStringDate()
                val key = FirebaseDatabase.getInstance().reference.child("session").child("third_grade").child(subjectCode)
                    .child(sessionKey).child("session_attendance").push().key
                val path = "/session/third_grade/$subjectCode/$sessionKey/session_attendance/$key"

                key?.run {
                    val sessionAttendance = SessionAttendance(name, dataSnapshot.getValue(User::class.java)!!.profile_picture_storage_path, date, this)
                    val childUpdate = HashMap<String, Any>()
                    childUpdate[path] = sessionAttendance
                    FirebaseDatabase.getInstance().reference.updateChildren(childUpdate)
                }
            }
        })
    }

}