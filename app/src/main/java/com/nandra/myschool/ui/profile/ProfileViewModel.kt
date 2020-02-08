package com.nandra.myschool.ui.profile

import com.ale.rainbowsdk.MyProfile
import com.ale.rainbowsdk.RainbowSdk

class ProfileViewModel {

    val profile: MyProfile by lazy {
        RainbowSdk.instance().myProfile()
    }
}