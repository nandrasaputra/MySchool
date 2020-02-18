package com.nandra.myschool.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.ale.infra.manager.fileserver.IFileProxy
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.ale.listener.IConnectionChanged
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.ConnectServerState
import com.nandra.myschool.utils.setupWithNavController
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), IConnectionChanged {

    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: MainActivityViewModel by viewModels()
    /*private val networkRequest = NetworkRequest.Builder()
        .build()
    lateinit var connectivityManager: ConnectivityManager
    private var networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            Log.d(LOG_DEBUG_TAG, "OnLost")
            viewModel.setNetworkState(NetworkState.DISCONNECTED)
        }

        override fun onAvailable(network: Network) {
            Log.d(LOG_DEBUG_TAG, "OnAvailable")
            viewModel.setNetworkState(NetworkState.CONNECTED)
        }

        override fun onUnavailable() {
            Log.d(LOG_DEBUG_TAG, "OnUnAvailable")
            viewModel.setNetworkState(NetworkState.UNAVAILABLE)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            Log.d(LOG_DEBUG_TAG, "OnLosing")
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        /*connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)*/

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        viewModel.connectServerState.observe(this, Observer {
            handleLoadingState(it)
        })
        /*viewModel.networkState.observe(this, Observer {
            handleNetworkState(it)
        })*/
        RainbowSdk.instance().connection().registerConnectionChangedListener(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.classroom_nav,R.navigation.chat_nav, R.navigation.profile_nav)

        val controller = main_activity_bottom_navigation_bar.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.main_activity_fragment_container,
            intent = intent
        )

        currentNavController = controller
    }

    private fun handleLoadingState(state: ConnectServerState) {
        when(state) {
            ConnectServerState.SUCCESS -> {
                main_activity_progress_bar.visibility = View.GONE
            }
            ConnectServerState.LOADING -> {
                main_activity_progress_bar.visibility = View.VISIBLE
            }
            else -> { }
        }
    }

    /*private fun handleNetworkState(state: NetworkState) {
        when(state) {
            NetworkState.CONNECTED -> {
                main_activity_connection_state.text = "CONNECTED"
            }
            NetworkState.DISCONNECTED -> {
                main_activity_connection_state.text = "LOST"
            }
            else -> {
                main_activity_connection_state.text = "UNAVAILABLE"
            }
        }
    }*/

    override fun onConnectionLost() {
        Handler(Looper.getMainLooper()).post {
                viewModel.setLoadingState(ConnectServerState.CONNECTION_ERROR)
            }
    }

    override fun onConnectionSucceed() {
        viewModel.updateFileStorage()
        viewModel.setLoadingState(ConnectServerState.SUCCESS)
    }

    override fun onDestroy() {
        super.onDestroy()
        //connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
