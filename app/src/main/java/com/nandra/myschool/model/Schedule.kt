package com.nandra.myschool.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Schedule (
    val subject_name: String = "",
    val subject_day: String = "",
    val subject_time: String = ""
)