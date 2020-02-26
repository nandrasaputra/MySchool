package com.nandra.myschool.model

import java.util.*

data class Material(
    val material_name: String = "",
    val material_mime: String = "",
    val material_uploader: String = "",
    val material_upload_date: String = "",
    val material_download_url: String = ""
)