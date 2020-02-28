package com.nandra.myschool.ui.classroom_detail.classroom_session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomSessionListAdapter
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.android.synthetic.main.classroom_session_fragment.*

class ClassroomSessionFragment : Fragment() {

    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()
    private lateinit var classroomSessionListAdapter: ClassroomSessionListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_session_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomDetailViewModel.sessionDataLoadState.observe(viewLifecycleOwner, Observer {
            handleSessionLoadState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        classroomSessionListAdapter = ClassroomSessionListAdapter()
        fragment_classroom_session_recycler_view.apply {
            adapter = classroomSessionListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun handleSessionLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                classroomDetailViewModel.getSessionList()
            }
            DataLoadState.LOADING -> { }
            DataLoadState.LOADED -> {
                classroomSessionListAdapter.submitList(classroomDetailViewModel.sessionList)
            }
            else -> {}
        }
    }
}