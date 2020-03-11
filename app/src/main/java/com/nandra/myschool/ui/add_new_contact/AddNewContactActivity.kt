package com.nandra.myschool.ui.add_new_contact

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nandra.myschool.R
import com.nandra.myschool.adapter.AddNewContactListAdapter
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.add_new_contact_activity.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddNewContactActivity : AppCompatActivity() {

    private lateinit var addNewContactAdapter: AddNewContactListAdapter
    private var searchView: SearchView? = null
    private val addNewContactViewModel: AddNewContactViewModel by viewModels()
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_contact_activity)

        setupToolbar()
        setupView()
        observeViewModel()
    }

    private fun setupView() {
        addNewContactAdapter = AddNewContactListAdapter()
        activity_add_new_contact_recycler_view.apply {
            adapter = addNewContactAdapter
            layoutManager = LinearLayoutManager(this@AddNewContactActivity)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(activity_add_new_contact_toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        activity_add_new_contact_toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_new_contact_menu, menu)
        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu?) {
        val searchItem = menu?.findItem(R.id.add_new_contact_search_menu_item)
        searchView = searchItem?.actionView as SearchView

        searchView?.run {
            this.setOnSearchClickListener {
                activity_add_new_contact_toolbar_title.visibility = View.GONE
                this.queryHint = "Search Contact"
            }

            this.setOnCloseListener {
                activity_add_new_contact_toolbar_title.visibility = View.VISIBLE
                addNewContactAdapter.wipeData()
                addNewContactViewModel.resetLoadState()
                false
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(LOG_DEBUG_TAG, "Query Change, text = $newText")
                    if (newText.isNullOrEmpty()) {
                        Log.d(LOG_DEBUG_TAG, "Is Null Or Empty")
                        lifecycleScope.launch {
                            searchJob?.run {
                                if (this.isActive) {
                                    this.cancel()
                                }
                                addNewContactAdapter.wipeData()
                            }
                        }
                    } else {
                        Log.d(LOG_DEBUG_TAG, "Is NOTT Null Or Empty")
                        lifecycleScope.launch {
                            searchJob?.run {
                                if (this.isActive) {
                                    this.cancel()
                                }
                            }
                            searchJob = launch {
                                delay(800L)
                                addNewContactViewModel.searchContact(newText)
                            }
                        }
                    }
                    return false
                }
            })
        }
    }

    private fun observeViewModel() {
        addNewContactViewModel.addNewContactLoadState.observe(this, Observer {
            handleLoadState(it)
        })
    }

    private fun handleLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                activity_add_new_contact_state_layout.visibility = View.VISIBLE
            }
            DataLoadState.LOADING -> {
                Log.d(LOG_DEBUG_TAG, "State LOADING")
            }
            DataLoadState.LOADED -> {
                Log.d(LOG_DEBUG_TAG, "State LOADED")
                activity_add_new_contact_state_layout.visibility = View.GONE
                addNewContactAdapter.submitList(addNewContactViewModel.contactList)
                addNewContactAdapter.notifyDataSetChanged()
            }
            else -> {
                Log.d(LOG_DEBUG_TAG, "State ELSE")
            }
        }
    }
}