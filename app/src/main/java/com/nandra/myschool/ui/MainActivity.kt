package com.nandra.myschool.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.ale.listener.IConnectionChanged
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility.LoadingState
import com.nandra.myschool.utils.setupWithNavController
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), IConnectionChanged {

    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        viewModel.loadingState.observe(this, Observer {
            handleLoadingState(it)
        })
        RainbowSdk.instance().connection().registerConnectionChangedListener(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.home_nav, R.navigation.chat_nav, R.navigation.classroom_nav, R.navigation.profile_nav)

        val controller = main_activity_bottom_navigation_bar.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.main_activity_fragment_container,
            intent = intent
        )

        currentNavController = controller
    }

    private fun handleLoadingState(state: LoadingState) {
        when(state) {
            LoadingState.SUCCESS -> {
                main_activity_progress_bar.visibility = View.GONE
            }
            LoadingState.LOADING -> {
                main_activity_progress_bar.visibility = View.VISIBLE
            }
            else -> { }
        }
    }

    override fun onConnectionLost() {
        Handler(Looper.getMainLooper()).post {
                viewModel.setLoadingState(LoadingState.CONNECTION_ERROR)
            }
    }

    override fun onConnectionSucceed() {
        viewModel.setLoadingState(LoadingState.SUCCESS)
    }
}
