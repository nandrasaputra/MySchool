package com.nandra.myschool.ui.main.classroom

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomListAdapter
import com.nandra.myschool.ui.ClassScheduleDialogFragment
import com.nandra.myschool.utils.Utility.DataLoadState
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.classroom_fragment.*

class ClassroomFragment : Fragment() {

    private val classroomViewModel: ClassroomViewModel by activityViewModels()
    private lateinit var classroomListAdapter: ClassroomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.classroom_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomViewModel.subjectDataLoadState.observe(viewLifecycleOwner, Observer {
            handleDataLoadState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(fragment_classroom_toolbar)
        }.apply {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        classroomListAdapter = ClassroomListAdapter()
        fragment_classroom_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = classroomListAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.classroom_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.classroom_schedule -> {
                ClassScheduleDialogFragment().show(childFragmentManager, "Schedule Dialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleDataLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                classroomViewModel.getSubjectList()
            }
            DataLoadState.LOADING -> { }
            DataLoadState.LOADED -> {
                classroomListAdapter.submitList(classroomViewModel.subjectList)
            }
            else -> {}
        }
    }
}