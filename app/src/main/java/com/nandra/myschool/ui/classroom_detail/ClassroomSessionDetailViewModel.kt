package com.nandra.myschool.ui.classroom_detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ale.rainbowsdk.RainbowSdk
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.nandra.myschool.model.Session
import com.nandra.myschool.model.SessionAttendance
import com.nandra.myschool.model.User
import com.nandra.myschool.repository.MySchoolRepository
import com.nandra.myschool.utils.Utility.ClassroomSessionEvent
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.getCurrentStringDate
import com.nandra.myschool.utils.Utility.nameBuilder
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

    val classroomSessionEvent: LiveData<ClassroomSessionEvent>
        get() = _classroomSessionEvent
    private val _classroomSessionEvent = MutableLiveData<ClassroomSessionEvent>(ClassroomSessionEvent.Idle)

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

    fun submitAttendance(subjectCode: String, sessionKey: String, grade: String) {

        val userId = RainbowSdk.instance().myProfile().connectedUser.id ?: ""
        repository.getUserDatabaseReference(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(LOG_DEBUG_TAG, "Submit Attendance Failed")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = nameBuilder(RainbowSdk.instance().myProfile().connectedUser)
                val date = getCurrentStringDate()
                val key = FirebaseDatabase.getInstance().reference.child("session").child("third_grade").child(subjectCode)
                    .child(sessionKey).child("session_attendance").push().key
                val path = "/session/third_grade/$subjectCode/$sessionKey/session_attendance/$key"

                key?.run {
                    val sessionAttendance = SessionAttendance(name, dataSnapshot.getValue(User::class.java)!!.profile_picture_storage_path, date, sessionKey
                        ,this, grade, subjectCode)
                    val childUpdate = HashMap<String, Any>()
                    childUpdate[path] = sessionAttendance
                    FirebaseDatabase.getInstance().reference.updateChildren(childUpdate)
                }
            }
        })
    }

    fun deleteAttendance(sessionAttendance: SessionAttendance) {
        val reference = FirebaseDatabase.getInstance().reference.child("session").child(sessionAttendance.grade).child(sessionAttendance.subjectCode)
            .child(sessionAttendance.session_key).child("session_attendance").child(sessionAttendance.attendance_key)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(LOG_DEBUG_TAG, "ClassroomSessionDetailViewModel Canceled : ${error.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.getValue<SessionAttendance>()
                if (children != null) {
                    reference.removeValue()
                } else {
                    Log.d(LOG_DEBUG_TAG, "Session Attendance Not Found")
                }
            }
        })
    }

    fun resetClassroomEvent() {
        _classroomSessionEvent.value = ClassroomSessionEvent.Idle
    }

    fun deleteSession(grade: String, subjectCode: String, sessionKey: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("session").child(grade).child(subjectCode).child(sessionKey)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(LOG_DEBUG_TAG, "ClassroomSessionDetailViewModel Canceled : ${error.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.getValue<Session>()
                if (children != null) {
                    reference.removeValue().addOnSuccessListener {
                        _classroomSessionEvent.value = ClassroomSessionEvent.SessionDeleteSuccess
                    }.addOnFailureListener {
                        _classroomSessionEvent.value = ClassroomSessionEvent.SessionDeleteFailed("Session Deletion Failed")
                    }
                } else {
                    Log.d(LOG_DEBUG_TAG, "Session Not Found")
                }
            }
        })
    }

}