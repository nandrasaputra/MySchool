package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.nandra.myschool.R
import com.nandra.myschool.model.SessionAttendance
import kotlinx.android.synthetic.main.classroom_session_detail_item.view.*

class ClassroomSessionDetailListAdapter(
    private val userRole: String,
    private val deleteClickCallback: (SessionAttendance) -> Unit
) : ListAdapter<SessionAttendance, ClassroomSessionDetailListAdapter.SessionDetailViewHolder>(sessionDetailDiffCallback) {

    private val storage = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_session_detail_item, parent, false)
        return SessionDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SessionDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(sessionAttendance: SessionAttendance) {
            itemView.apply {
                activity_classroom_session_detail_name.text = sessionAttendance.name
                activity_classroom_session_detail_date.text = sessionAttendance.date
                if (userRole == "user_teacher") {
                    activity_classroom_session_detail_remove_button.visibility = View.VISIBLE
                    activity_classroom_session_detail_remove_button.setOnClickListener {
                        deleteClickCallback.invoke(sessionAttendance)
                    }
                } else {
                    activity_classroom_session_detail_remove_button.visibility = View.GONE
                }
            }
            Glide.with(itemView.context)
                .load(storage.getReferenceFromUrl(sessionAttendance.profile_path))
                .placeholder(R.drawable.ic_profile)
                .into(itemView.activity_classroom_session_detail_image)
        }
    }

    companion object {
        private val sessionDetailDiffCallback = object : DiffUtil.ItemCallback<SessionAttendance>() {
            override fun areItemsTheSame(oldItem: SessionAttendance, newItem: SessionAttendance): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: SessionAttendance, newItem: SessionAttendance): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}