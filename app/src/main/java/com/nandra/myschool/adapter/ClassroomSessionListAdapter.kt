package com.nandra.myschool.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nandra.myschool.R
import com.nandra.myschool.model.Session
import com.nandra.myschool.ui.classroom_detail.ClassroomSessionDetailActivity
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.classroom_session_item.view.*

class ClassroomSessionListAdapter(
    val userRole: String
) : ListAdapter<Session, ClassroomSessionListAdapter.ClassroomSessionViewHolder>(classroomSessionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomSessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_session_item, parent, false)
        return ClassroomSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassroomSessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ClassroomSessionViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(session: Session) {
            itemView.apply {
                val status = "Status : " + session.session_status
                val initiator = "Initiator : " + session.session_initiator_name
                fragment_classroom_session_item_topic.text = session.session_topic
                fragment_classroom_session_item_date.text = session.session_date
                fragment_classroom_session_item_description.text = session.session_description
                fragment_classroom_session_item_status.text = status
                fragment_classroom_session_item_initiator.text = initiator
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ClassroomSessionDetailActivity::class.java).apply {
                    putExtra(Utility.EXTRA_SESSION_KEY, session.session_key)
                    putExtra(Utility.EXTRA_SUBJECT_CODE, session.subject_code)
                    putExtra(Utility.EXTRA_USER_ROLE, userRole)
                    putExtra(Utility.EXTRA_GRADE, session.grade)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        private val classroomSessionDiffCallback = object : DiffUtil.ItemCallback<Session>() {
            override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
                return oldItem.session_key == newItem.session_key
            }
        }
    }
}