package com.nandra.myschool.ui.classroom_detail.classroom_material

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R

class ClassroomMaterialFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_material_fragment, container, false)
    }

}