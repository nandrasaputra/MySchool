package com.nandra.myschool

import android.app.Application
import android.util.Log
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.utils.Utility

class MySchoolApp : Application() {

    private val appID = "***REMOVED***"
    private val appSecret = "***REMOVED***"

    override fun onCreate() {
        super.onCreate()
        RainbowSdk.instance().initialize(this, appID, appSecret)
    }
}