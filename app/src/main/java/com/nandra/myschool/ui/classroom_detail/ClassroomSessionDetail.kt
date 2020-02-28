package com.nandra.myschool.ui.classroom_detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomSessionDetailListAdapter
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.EXTRA_SESSION_KEY
import com.nandra.myschool.utils.Utility.EXTRA_SUBJECT_CODE
import com.nandra.myschool.utils.Utility.EXTRA_USER_ROLE
import kotlinx.android.synthetic.main.classroom_session_detail.*

class ClassroomSessionDetail : AppCompatActivity() {

    private val classroomSessionDetailViewModel: ClassroomSessionDetailViewModel by viewModels()
    lateinit var classroomSessionDetailListAdapter: ClassroomSessionDetailListAdapter
    private var userRole = ""
    private var sessionKey = ""
    private var subjectCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classroom_session_detail)

        userRole = intent.getStringExtra(EXTRA_USER_ROLE) ?: ""
        sessionKey = intent.getStringExtra(EXTRA_SESSION_KEY) ?: ""
        subjectCode = intent.getStringExtra(EXTRA_SUBJECT_CODE) ?: ""

        setSupportActionBar(activity_classroom_session_detail_toolbar)

        classroomSessionDetailListAdapter = ClassroomSessionDetailListAdapter()
        activity_classroom_session_detail_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ClassroomSessionDetail)
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
            R.id.classroom_session_detail_close_session -> {
                true
            }
            R.id.classroom_session_detail_delete_seesion -> {
                true
            }
            R.id.classroom_session_detail_submit_attendance -> {
                classroomSessionDetailViewModel.submitAttendance(subjectCode, sessionKey)
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
}