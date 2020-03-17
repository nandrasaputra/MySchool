package com.nandra.myschool.ui.classroom_detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomSessionDetailListAdapter
import com.nandra.myschool.model.SessionAttendance
import com.nandra.myschool.utils.Utility.ClassroomSessionEvent
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.EXTRA_GRADE
import com.nandra.myschool.utils.Utility.EXTRA_SESSION_KEY
import com.nandra.myschool.utils.Utility.EXTRA_SUBJECT_CODE
import com.nandra.myschool.utils.Utility.EXTRA_USER_ROLE
import kotlinx.android.synthetic.main.classroom_session_detail.*

class ClassroomSessionDetailActivity : AppCompatActivity() {

    private val classroomSessionDetailViewModel: ClassroomSessionDetailViewModel by viewModels()
    private lateinit var classroomSessionDetailListAdapter: ClassroomSessionDetailListAdapter
    private var userRole = ""
    private var sessionKey = ""
    private var subjectCode = ""
    private var grade = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom_session_detail)

        userRole = intent.getStringExtra(EXTRA_USER_ROLE) ?: ""
        sessionKey = intent.getStringExtra(EXTRA_SESSION_KEY) ?: ""
        subjectCode = intent.getStringExtra(EXTRA_SUBJECT_CODE) ?: ""
        grade = intent.getStringExtra(EXTRA_GRADE) ?: ""

        setSupportActionBar(activity_classroom_session_detail_toolbar)

        classroomSessionDetailListAdapter = ClassroomSessionDetailListAdapter(userRole, ::onDeleteAttendance)
        activity_classroom_session_detail_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ClassroomSessionDetailActivity)
            adapter = classroomSessionDetailListAdapter
        }

        classroomSessionDetailViewModel.attendanceLoadState.observe(this, Observer {
            handleAttendanceLoadState(it)
        })

        setupView()
        observeViewModel()
    }

    private fun setupView() {
        setupToolbar()
    }

    private fun observeViewModel() {
        classroomSessionDetailViewModel.classroomSessionEvent.observe(this, Observer {
            handleClassroomSessionDetailEvent(it)
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(activity_classroom_session_detail_toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        activity_classroom_session_detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        activity_classroom_session_detail_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (userRole == "user_teacher") {
            menuInflater.inflate(R.menu.classroom_session_detail_teacher, menu)
        } else {
            menuInflater.inflate(R.menu.classroom_session_detail_student, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.classroom_session_detail_close_session_menu_item -> {
                classroomSessionDetailViewModel.closeSession(grade, subjectCode, sessionKey)
                true
            }
            R.id.classroom_session_detail_delete_seesion_menu_item -> {
                classroomSessionDetailViewModel.deleteSession(grade, subjectCode, sessionKey)
                true
            }
            R.id.classroom_session_detail_submit_attendance_menu_item -> {
                classroomSessionDetailViewModel.submitAttendance(subjectCode, sessionKey, grade)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun handleAttendanceLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                classroomSessionDetailViewModel.getAttendanceList(subjectCode, sessionKey)
            }
            DataLoadState.LOADING -> { }
            DataLoadState.LOADED -> {
                classroomSessionDetailListAdapter.submitList(classroomSessionDetailViewModel.attendanceList)
            }
            else -> {}
        }
    }

    private fun onDeleteAttendance(sessionAttendance: SessionAttendance) {
        classroomSessionDetailViewModel.deleteAttendance(sessionAttendance)
    }

    private fun handleClassroomSessionDetailEvent(event: ClassroomSessionEvent) {
        when(event) {
            ClassroomSessionEvent.SessionAttendanceDeleteSuccess -> { }
            is ClassroomSessionEvent.SessionAttendanceDeleteFailed -> {}
            ClassroomSessionEvent.SessionDeleteSuccess -> {
                Toast.makeText(this, "Session Deletion Success", Toast.LENGTH_SHORT).show()
                classroomSessionDetailViewModel.resetClassroomEvent()
                this.finish()
            }
            is ClassroomSessionEvent.SessionDeleteFailed -> {
                Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
                classroomSessionDetailViewModel.resetClassroomEvent()
            }
            ClassroomSessionEvent.CloseSessionSuccess -> {
                Toast.makeText(this, "Session Closed", Toast.LENGTH_SHORT).show()
                classroomSessionDetailViewModel.resetClassroomEvent()
            }
            is ClassroomSessionEvent.CloseSessionFailed -> {
                Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
                classroomSessionDetailViewModel.resetClassroomEvent()
            }
            ClassroomSessionEvent.SubmitAttendanceSuccess -> {
                Toast.makeText(this, "Submit Attendance Success", Toast.LENGTH_SHORT).show()
                classroomSessionDetailViewModel.resetClassroomEvent()
            }
            is ClassroomSessionEvent.SubmitAttendanceFailed -> {
                Toast.makeText(this, event.errorMessage, Toast.LENGTH_SHORT).show()
                classroomSessionDetailViewModel.resetClassroomEvent()
            }
            ClassroomSessionEvent.Idle -> {}
        }
    }
}