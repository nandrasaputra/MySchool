package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nandra.myschool.R
import com.nandra.myschool.model.Schedule
import kotlinx.android.synthetic.main.schedule_item.view.*

class ScheduleListAdapter : ListAdapter<Schedule, ScheduleListAdapter.ScheduleViewHolder>(scheduleDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(schedule: Schedule) {
            val scheduleTime = schedule.subject_day + ",\n" + schedule.subject_time
            itemView.schedule_item_name.text = schedule.subject_name
            itemView.schedule_item_hour.text = scheduleTime
        }
    }

    companion object {
        private val scheduleDiffCallback = object : DiffUtil.ItemCallback<Schedule>() {
            override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
                return oldItem.subject_name == newItem.subject_name
            }
        }
    }
}