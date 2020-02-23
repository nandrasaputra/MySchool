package com.nandra.myschool.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.nandra.myschool.R
import com.nandra.myschool.utils.Utility.IAddNewChannelItem
import kotlinx.android.synthetic.main.add_new_channel_item_dialog_fragment.*

class AddNewChannelItemDialogFragment(private val addNewChannelItemInterface: IAddNewChannelItem) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_new_channel_item_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_fragment_add_new_channel_item_send_button.setOnClickListener {
            onSendButtonClicked()
        }
        dialog_fragment_add_new_channel_item_cancel_button.setOnClickListener {
            onCancelButtonClicked()
        }
    }

    private fun onSendButtonClicked() {
        val message = dialog_fragment_add_new_channel_item_text.text.toString()
        if (message.isNotEmpty()) {
            addNewChannelItemInterface.onSendButtonClicked(message)
            dialog?.dismiss()
        } else {
            Toast.makeText(activity, "Message Cannot Be Blank!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCancelButtonClicked() {
        addNewChannelItemInterface.onCancelButtonClicked()
        dialog?.dismiss()
    }
}