package com.nandra.myschool.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.nandra.myschool.R
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.add_new_session_dialog_fragment.*

class AddNewSessionDialogFragment : DialogFragment() {

    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_new_session_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_new_session_add_button.setOnClickListener {
            val topic = add_new_session_topic_text.text.toString()
            val description = add_new_session_description_text.text.toString()

            if (topic.isNotEmpty() and description.isNotEmpty()) {
                classroomDetailViewModel.addNewSession(topic, description)
                dialog?.dismiss()
            } else {
                Toast.makeText(activity, "Topic and Description Cannot be Empty!", Toast.LENGTH_SHORT).show()
            }
        }
        add_new_session_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }
    }
}