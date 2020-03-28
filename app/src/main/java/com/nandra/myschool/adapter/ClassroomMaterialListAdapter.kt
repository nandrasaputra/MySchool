package com.nandra.myschool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nandra.myschool.R
import com.nandra.myschool.model.Material
import com.nandra.myschool.utils.Utility.ClassroomDetailPopupMenuCallback
import kotlinx.android.synthetic.main.classroom_material_fragment_item.view.*

class ClassroomMaterialListAdapter(
    private val hamburgerClickCallback : (ClassroomDetailPopupMenuCallback) -> Unit,
    private val isTeacherAccount: Boolean
) : ListAdapter<Material, ClassroomMaterialListAdapter.ClassroomMaterialViewHolder>(classroomMaterialDiffCallback) {

    private var currentMaterial = Material()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomMaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.classroom_material_fragment_item, parent, false)
        return ClassroomMaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassroomMaterialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClassroomMaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(material: Material) {
            currentMaterial = Material()
            itemView.apply {
                fragment_classroom_material_item_date.text = material.material_upload_date
                fragment_classroom_material_item_file_name.text = material.material_name
            }
            if (isTeacherAccount) {
                itemView.fragment_classroom_material_item_hamburger.visibility = View.VISIBLE
                itemView.fragment_classroom_material_item_hamburger.setOnClickListener {
                    PopupMenu(itemView.context, it).apply {
                        this.menuInflater.inflate(R.menu.classroom_material_popup_menu, this.menu)

                        this.setOnMenuItemClickListener {menuItem ->
                            return@setOnMenuItemClickListener when(menuItem.itemId) {
                                R.id.classroom_material_download_menu_item -> {
                                    hamburgerClickCallback.invoke(ClassroomDetailPopupMenuCallback.OnDownloadClicked(material))
                                    true
                                }
                                R.id.classroom_material_delete_menu_item -> {
                                    hamburgerClickCallback.invoke(ClassroomDetailPopupMenuCallback.OnDeleteClicked(material))
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                    }.show()
                }
            } else {
                itemView.fragment_classroom_material_item_hamburger.visibility = View.GONE
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