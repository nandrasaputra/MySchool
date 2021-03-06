package com.nandra.myschool.ui.main.classroom

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomListAdapter
import com.nandra.myschool.ui.ClassScheduleDialogFragment
import com.nandra.myschool.ui.main.MainActivityViewModel
import com.nandra.myschool.utils.Utility.ConnectServerState
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.classroom_fragment.*

class ClassroomFragment : Fragment() {

    private val classroomViewModel: ClassroomViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var classroomListAdapter: ClassroomListAdapter
    private var searchView: SearchView? = null

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
        classroomViewModel.userLoadState.observe(viewLifecycleOwner, Observer {
            handleUserLoadState(it)
        })
        mainActivityViewModel.connectServerState.observe(viewLifecycleOwner, Observer {
            handleConnectState(it)
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
        setupSearchView(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.classroom_schedule_menu_item -> {
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
                classroomListAdapter.submitAndUpdateList(classroomViewModel.subjectList)
            }
            else -> {}
        }
    }

    private fun handleUserLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                classroomViewModel.getUser()
            }
            DataLoadState.LOADING -> { }
            DataLoadState.LOADED -> {
                classroomListAdapter.submitUser(classroomViewModel.currentUser)
            }
            else -> {}
        }
    }

    private fun handleConnectState(state: ConnectServerState) {
        when(state) {
            ConnectServerState.UNKNOWN -> {
                mainActivityViewModel.checkServerLoadingState()
            }
            ConnectServerState.LOADING -> {
                fragment_classroom_shimmer_veil.visibility = View.VISIBLE
                fragment_classroom_shimmer_layout.visibility = View.VISIBLE
                fragment_classroom_shimmer_layout.startShimmer()
            }
            ConnectServerState.SUCCESS -> {
                fragment_classroom_shimmer_veil.visibility = View.GONE
                fragment_classroom_shimmer_layout.visibility = View.GONE
                fragment_classroom_shimmer_layout.stopShimmer()
            }
            ConnectServerState.CONNECTION_ERROR -> { }
        }
    }

    private fun setupSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.classroom_search_menu_item)
        searchView = searchItem?.actionView as SearchView

        searchView?.run {
            this.setOnSearchClickListener {
                fragment_classroom_toolbar_title.visibility = View.GONE
                menu.findItem(R.id.classroom_schedule_menu_item).isVisible = false
                this.queryHint = "Search Subject"
                classroomViewModel.isSearchViewOnCloseHandled = false
                Log.d(LOG_DEBUG_TAG, "isSearchViewOnCloseHandled = false")
            }

            this.setOnCloseListener {
                fragment_classroom_toolbar_title.visibility = View.VISIBLE
                menu.findItem(R.id.classroom_schedule_menu_item).isVisible = true
                classroomListAdapter.restoreList()
                classroomViewModel.isSearchViewOnCloseHandled = true
                Log.d(LOG_DEBUG_TAG, "isSearchViewOnCloseHandled = true")
                false
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.run {
                        classroomListAdapter.filter.filter(this.toLowerCase())
                    }
                    return true
                }
            })
        }
    }
}