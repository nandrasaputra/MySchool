package com.nandra.myschool.utils

object Utility {
    const val EXTRA_MESSAGE = "message"
    const val LOG_DEBUG_TAG = "MySchool"

    enum class LoadingState {
        LOADING,
        SUCCESS,
        CONNECTION_ERROR
    }
}