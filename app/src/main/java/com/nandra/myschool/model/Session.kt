package com.nandra.myschool.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Session (
    val session_topic: String = "",
    val session_description: String = "",
    val session_initiator_name: String = "",
    val session_date: String = "",
    val session_status: String = "",
    val session_key: String = "",
    val subject_code: String = "",
    val session_attendance_list: List<SessionAttendance> = listOf()
)