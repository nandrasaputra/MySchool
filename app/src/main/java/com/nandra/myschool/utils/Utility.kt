package com.nandra.myschool.utils

import android.app.Application
import android.net.Uri
import android.webkit.MimeTypeMap
import com.ale.infra.contact.IRainbowContact
import java.text.SimpleDateFormat
import java.util.*

object Utility {
    const val EXTRA_SUBJECT_NAME = "subject_name"
    const val EXTRA_SUBJECT_CODE = "subject_code"
    const val EXTRA_SUBJECT_ID = "subject_id"
    const val EXTRA_SESSION_KEY = "session_id"
    const val EXTRA_USER_ROLE = "user_role"
    const val EXTRA_JID = "jid"
    const val LOG_DEBUG_TAG = "MySchool"

    enum class ConnectServerState {
        UNKNOWN,
        LOADING,
        SUCCESS,
        CONNECTION_ERROR
    }

    enum class DataLoadState {
        LOADED,
        UNLOADED,
        LOADING,
        ERROR
    }

    enum class NetworkState {
        CONNECTED,
        DISCONNECTED,
        UNAVAILABLE
    }

    enum class UploadFileState {
        IDLE,
        UPLOADING,
        UPLOADED,
        FAILED,
        CANCELED
    }

    fun Date?.convertToString() : String {
        return if (this != null) {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            simpleDateFormat.format(this)
        } else {
            ""
        }
    }

    fun nameBuilder(contact: IRainbowContact) : String {
        val firstName = contact.firstName
        val lastName = contact.lastName
        return if(firstName!= null && lastName != null) {
            "$firstName $lastName"
        } else if (firstName != null && lastName == null) {
            firstName
        } else if(firstName == null && lastName != null) {
            lastName
        } else {
            "No Name"
        }
    }

    fun getCurrentStringDate() : String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(calendar.time)
    }

    fun getMimeTypeFromUri(app: Application, uri: Uri) : String? {
        val contentResolver = app.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return contentResolver.getType(uri)
    }

    interface IAddNewChannelItem {
        fun onSendButtonClicked(message: String)
        fun onCancelButtonClicked()
    }

    interface IUploadFile {
        fun onUploadButtonClicked()
        fun onCancelButtonClicked()
    }

    interface IAddNewSession {
        fun onAddSessionButtonClicked(topic: String, description: String)
        fun onCancelSessionButtonClicked()
    }

    sealed class AddContactToRoasterState {
        class Loading(val adapterPosition: Int) : AddContactToRoasterState()
        class Finished(val adapterPosition: Int, val name: String) : AddContactToRoasterState()
        class Failed(val errorMessage: String, val adapterPosition: Int) : AddContactToRoasterState()
        object Idle : AddContactToRoasterState()
    }
}