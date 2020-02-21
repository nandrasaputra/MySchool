package com.nandra.myschool.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Subject(
    var channel_id: String = "",
    var cover_path: String = "",
    var subject_code: String = "",
    var subject_description: String = "",
    var subject_name: String = "",
    var subject_id: Int = 0,
    var teachers: List<Teacher> = listOf()
)