package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nandra.myschool.R
import com.nandra.myschool.model.Material
import kotlinx.android.synthetic.main.classroom_material_fragment_item.view.*

class ClassroomMaterialListAdapter(
    val clickDownloadCallback : (Material) -> Unit
) : ListAdapter<Material, ClassroomMaterialListAdapter.ClassroomMaterialViewHolder>(classroomMaterialDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomMaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_material_fragment_item, parent, false)
        return ClassroomMaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassroomMaterialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClassroomMaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(material: Material) {
            itemView.apply {
                fragment_classroom_material_item_date.text = material.material_upload_date
                fragment_classroom_material_item_file_name.text = material.material_name
            }
            itemView.fragment_classroom_material_item_download.setOnClickListener {
                clickDownloadCallback(material)
            }
        }
    }

    companion object {
        private val classroomMaterialDiffCallback = object : DiffUtil.ItemCallback<Material>() {
            override fun areItemsTheSame(oldItem: Material, newItem: Material): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Material, newItem: Material): Boolean {
                return oldItem.material_name == newItem.material_name
            }
        }
    }

}