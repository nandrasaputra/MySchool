package com.nandra.myschool.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Material(
    val material_name: String = "",
    val material_mime: String = "",
    val material_upload_date: String = "",
    val material_download_url: String = "",
    val material_key: String = "",
    val material_database_path: String = "",
    val material_storage_path: String = ""
)