package com.nandra.myschool.ui

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.nandra.myschool.R
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import com.nandra.myschool.utils.Utility.UploadFileState
import com.nandra.myschool.utils.Utility.UploadFileState.*
import kotlinx.android.synthetic.main.upload_file_confirmation_dialog_fragment.*

class UploadFileConfirmationDialogFragment(
    private val uri: Uri,
    private val mimeType: String
) : DialogFragment() {

    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.upload_file_confirmation_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        upload_file_confirmation_upload_button.setOnClickListener {
            val materialName = upload_file_confirmation_material_name_edit_text.text.toString()
            if (materialName.isNotEmpty()) {
                classroomDetailViewModel.uploadFileToFirebase(uri, materialName, mimeType, getFileExtension(uri))
            } else {
                Toast.makeText(activity, "Material Name Cannot Be Empty!", Toast.LENGTH_SHORT).show()
            }
        }
        upload_file_confirmation_cancel_button.setOnClickListener {
            when {
                classroomDetailViewModel.isUploadInProgress() -> {
                    classroomDetailViewModel.attemptToCancelUpload()
                    classroomDetailViewModel.resetUploadFileState()
                    Toast.makeText(activity, "Upload Canceled", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss()
                }
                else -> {
                    dialog?.dismiss()
                }
            }
        }
        classroomDetailViewModel.uploadFileState.observe(viewLifecycleOwner, Observer {
            handleUploadFileState(it)
            Log.d(LOG_DEBUG_TAG, it.toString())
        })
    }

    private fun getFileExtension(uri: Uri) : String {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity!!.contentResolver.getType(uri))!!
    }

    private fun handleUploadFileState(state: UploadFileState) {
        when(state) {
            IDLE -> {}
            UPLOADING -> {
                upload_file_confirmation_upload_button.visibility = View.GONE
                upload_file_confirmation_material_name_edit_text.visibility = View.GONE
                upload_file_confirmation_progress_bar.visibility = View.VISIBLE
            }
            UPLOADED -> {
                classroomDetailViewModel.resetUploadFileState()
                Toast.makeText(activity, "Upload Success", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }
            FAILED -> {
                classroomDetailViewModel.resetUploadFileState()
                Toast.makeText(activity, "Upload Failed", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }
            CANCELED -> {
                classroomDetailViewModel.resetUploadFileState()
                Toast.makeText(activity, "Upload Canceled", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (classroomDetailViewModel.isUploadInProgress()) {
            classroomDetailViewModel.attemptToCancelUpload()
            classroomDetailViewModel.resetUploadFileState()
            Toast.makeText(activity, "Upload Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}