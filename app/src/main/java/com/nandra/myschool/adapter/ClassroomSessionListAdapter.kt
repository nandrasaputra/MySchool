package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.channel.ChannelItem
import com.nandra.myschool.R
import com.nandra.myschool.model.Session
import kotlinx.android.synthetic.main.classroom_session_item.view.*

class ClassroomSessionListAdapter : ListAdapter<Session, ClassroomSessionListAdapter.ClassroomSessionViewHolder>(classroomSessionDiffCallback) {

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