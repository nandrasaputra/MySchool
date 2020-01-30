package com.nandra.myschool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nandra.myschool.R
import com.nandra.myschool.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    HomeFragment.newInstance()
                )
                .commitNow()
        }
    }

}
