package com.nandra.myschool

import android.app.Application
import com.ale.rainbowsdk.RainbowSdk

class MySchoolApp : Application() {

    private val appID = BuildConfig.RAINBOW_API_APP_ID
    private val appSecret = BuildConfig.RAINBOW_API_APP_SECRET

    override fun onCreate() {
        super.onCreate()
        RainbowSdk.instance().initialize(this, appID, appSecret)
    }
}