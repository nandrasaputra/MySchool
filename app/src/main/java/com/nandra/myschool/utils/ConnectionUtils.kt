package com.nandra.myschool.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

fun isInternetConnectionAvailable(app: Application) : Boolean {
    val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}
