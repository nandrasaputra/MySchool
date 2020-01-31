package com.nandra.myschool.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.nandra.myschool.R
import com.nandra.myschool.utils.setupWithNavController
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
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
}
