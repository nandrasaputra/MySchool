package com.nandra.myschool

import android.app.Application
import com.ale.rainbowsdk.RainbowSdk

class MySchoolApp : Application() {

    private val appID = "***REMOVED***"
    private val appSecret = "***REMOVED***"

    override fun onCreate() {
        super.onCreate()
        RainbowSdk.instance().initialize(this, appID, appSecret)
    }
}