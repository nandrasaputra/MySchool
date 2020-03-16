package com.nandra.myschool.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SessionAttendance(
    val name: String = "",
    val profile_path: String = "",
    val date: String = "",
    val session_key: String = "",
    val attendance_key: String = "",
    val grade: String = "",
    val subjectCode: String = ""
)