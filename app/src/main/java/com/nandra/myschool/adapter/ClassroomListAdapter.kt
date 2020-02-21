package com.nandra.myschool.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.nandra.myschool.R
import com.nandra.myschool.model.Subject
import com.nandra.myschool.utils.Utility
import kotlinx.android.synthetic.main.classroom_fragment_item.view.*

class ClassroomListAdapter : ListAdapter<Subject, ClassroomListAdapter.ClassroomViewHolder>(classroomDiffCallback) {

    val storage = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_fragment_item, parent, false)
        return ClassroomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClassroomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(subject: Subject) {
            itemView.fragment_classroom_item_content_title.text = subject.subject_name
            itemView.fragment_classroom_item_content_description.text = subject.subject_description
            Glide.with(itemView.context)
                .load(storage.getReferenceFromUrl(subject.cover_path))
                .into(itemView.fragment_classroom_item_class_image)
            val teachers = subject.teachers
            for (teacher in teachers) {
                Log.d(Utility.LOG_DEBUG_TAG, teacher.profile_picture_storage_path)
            }
            when (teachers.size) {
                1 -> {
                    itemView.fragment_classroom_item_teacher_1.visibility = View.VISIBLE
                    itemView.fragment_classroom_item_teacher_2.visibility = View.GONE
                    itemView.fragment_classroom_item_teacher_3.visibility = View.GONE
                    Glide.with(itemView.context)
                        .load(storage.getReferenceFromUrl(teachers[0].profile_picture_storage_path))
                        .into(itemView.fragment_classroom_item_teacher_1)
                }
                2 -> {
                    itemView.fragment_classroom_item_teacher_1.visibility = View.VISIBLE
                    itemView.fragment_classroom_item_teacher_2.visibility = View.VISIBLE
                    itemView.fragment_classroom_item_teacher_3.visibility = View.GONE
                    Glide.with(itemView.context)
                        .load(storage.getReferenceFromUrl(teachers[0].profile_picture_storage_path))
                        .into(itemView.fragment_classroom_item_teacher_1)
                    Glide.with(itemView.context)
                        .load(storage.getReferenceFromUrl(teachers[1].profile_picture_storage_path))
                        .into(itemView.fragment_classroom_item_teacher_2)
                }
                3 -> {
                    itemView.fragment_classroom_item_teacher_1.visibility = View.VISIBLE
                    itemView.fragment_classroom_item_teacher_2.visibility = View.VISIBLE
                    itemView.fragment_classroom_item_teacher_3.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(storage.getReferenceFromUrl(teachers[0].profile_picture_storage_path))
                        .into(itemView.fragment_classroom_item_teacher_1)
                    Glide.with(itemView.context)
                        .load(storage.getReferenceFromUrl(teachers[1].profile_picture_storage_path))
                        .into(itemView.fragment_classroom_item_teacher_2)
                    Glide.with(itemView.context)
                        .load(storage.getReferenceFromUrl(teachers[2].profile_picture_storage_path))
                        .into(itemView.fragment_classroom_item_teacher_3)
                }
            }
        }
    }
    companion object {
        private val classroomDiffCallback = object : DiffUtil.ItemCallback<Subject>() {
            override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
                return oldItem.subject_id == newItem.subject_id
            }
        }
    }
}