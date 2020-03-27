package com.nandra.myschool.ui.classroom_detail.classroom_feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomFeedListAdapter
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.android.synthetic.main.classroom_feed_fragment.*

class ClassroomFeedFragment : Fragment() {

    private lateinit var classroomFeedListAdapter: ClassroomFeedListAdapter
    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomDetailViewModel.detailSubjectDataLoadState.observe(viewLifecycleOwner, Observer {
            handleDetailLoadState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        classroomDetailViewModel.unregisterAnyChannelFetchItemListener()
        classroomDetailViewModel.changeSubjectDataLoadState(DataLoadState.UNLOADED)
    }

    private fun setupView() {
        val isTeacherAccount = classroomDetailViewModel.userRole == "user_teacher"
        classroomFeedListAdapter = ClassroomFeedListAdapter(::onFeedItemFailureCallback, ::hamburgerClickCallback, isTeacherAccount)
        fragment_classroom_feed_recycler_view.apply {
            adapter = classroomFeedListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun handleDetailLoadState(state: DataLoadState) {
        when (state) {
            DataLoadState.UNLOADED -> { }
            DataLoadState.LOADING -> {
                fragment_classroom_feed_shimmer_veil.visibility = View.VISIBLE
                fragment_classroom_feed_shimmer_layout.visibility = View.VISIBLE
                fragment_classroom_feed_shimmer_layout.startShimmer()
            }
            DataLoadState.LOADED -> {
                classroomFeedListAdapter.submitList(classroomDetailViewModel.classroomFeedItemList)
                classroomFeedListAdapter.notifyDataSetChanged()
                fragment_classroom_feed_shimmer_veil.visibility = View.GONE
                fragment_classroom_feed_shimmer_layout.visibility = View.GONE
                fragment_classroom_feed_shimmer_layout.stopShimmer()
            }
            else -> { }
        }
    }

    private fun hamburgerClickCallback(callback: Utility.ClassroomDetailPopupMenuCallback) {
        when(callback) {
            is Utility.ClassroomDetailPopupMenuCallback.OnDeleteClicked -> {

            }
        }
    }

    private fun onFeedItemFailureCallback() {
        classroomDetailViewModel.refreshChannelItemList()
    }
}