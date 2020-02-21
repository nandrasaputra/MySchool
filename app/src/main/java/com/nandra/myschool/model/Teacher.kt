package com.nandra.myschool.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Teacher(
    var email: String = "",
    var first_name: String = "",
    var last_name: String = "",
    var id: String = "",
    var profile_picture_storage_path: String = "")