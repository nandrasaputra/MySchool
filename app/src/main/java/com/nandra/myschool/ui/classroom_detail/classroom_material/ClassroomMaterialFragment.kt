package com.nandra.myschool.ui.classroom_detail.classroom_material

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomMaterialListAdapter
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.classroom_material_fragment.*

class ClassroomMaterialFragment : Fragment() {

    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()
    private lateinit var classroomMaterialListAdapter: ClassroomMaterialListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_material_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomDetailViewModel.materialDataLoadState.observe(viewLifecycleOwner, Observer {
            handleMaterialLoadState(it)
            Log.d(LOG_DEBUG_TAG, "Material State : ${it.toString()}")
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        classroomMaterialListAdapter = ClassroomMaterialListAdapter()
        fragment_classroom_material_recycler_view.apply {
            adapter = classroomMaterialListAdapter
            layoutManager = GridLayoutManager(activity, 3)
        }
    }

    private fun handleMaterialLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                Log.d(LOG_DEBUG_TAG, "Material State UNLOADED")
                classroomDetailViewModel.getMaterialList()
            }
            DataLoadState.LOADING -> {
                Log.d(LOG_DEBUG_TAG, "Material State LOADING")
            }
            DataLoadState.LOADED -> {
                Log.d(LOG_DEBUG_TAG, "Material State LOADED")
                Log.d(LOG_DEBUG_TAG, "MAterial Size = ${classroomDetailViewModel.materialList.size}")
                classroomMaterialListAdapter.submitList(classroomDetailViewModel.materialList)
                classroomMaterialListAdapter.notifyDataSetChanged()
            }
            else -> {}
        }
    }
}