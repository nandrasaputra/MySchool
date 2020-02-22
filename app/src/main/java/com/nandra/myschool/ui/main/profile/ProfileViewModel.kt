package com.nandra.myschool.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ale.rainbowsdk.MyProfile
import com.ale.rainbowsdk.RainbowSdk

class ProfileViewModel(val app: Application) : AndroidViewModel(app) {

    private var cachedProfile: MyProfile? = null

    fun getProfile() : MyProfile {
        return if (cachedProfile != null) {
            cachedProfile!!
        } else {
            updateProfile()
            cachedProfile!!
        }
    }

    fun updateProfile() {
        cachedProfile = RainbowSdk.instance().myProfile()
    }
}