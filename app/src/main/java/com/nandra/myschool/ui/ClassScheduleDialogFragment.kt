package com.nandra.myschool.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ScheduleListAdapter
import com.nandra.myschool.ui.main.classroom.ClassroomViewModel
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.android.synthetic.main.schedule_dialog_fragment.*

class ClassScheduleDialogFragment() : DialogFragment() {

    private lateinit var scheduleListAdapter: ScheduleListAdapter
    private val classroomViewModel: ClassroomViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.schedule_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomViewModel.scheduleDataLoadState.observe(viewLifecycleOwner, Observer {
            handleLoadState(it)
        })
        schedule_dialog_fragment_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scheduleListAdapter = ScheduleListAdapter()
        schedule_dialog_fragment_recycle_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = scheduleListAdapter
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
    }

    private fun handleLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.LOADED -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Schedule Loaded")
                Log.d(Utility.LOG_DEBUG_TAG, classroomViewModel.scheduleList.size.toString())
                scheduleListAdapter.submitList(classroomViewModel.scheduleList)
            }
            DataLoadState.UNLOADED -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Schedule UnLoaded")
                classroomViewModel.getScheduleList()
            }
            DataLoadState.LOADING -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Schedule Loading")
            }
            DataLoadState.ERROR -> {
                Log.d(Utility.LOG_DEBUG_TAG, "Schedule Error")
            }
        }
    }
}