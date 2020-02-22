package com.nandra.myschool.ui.classroom_detail.classroom_session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nandra.myschool.R

class ClassroomSessionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_session_fragment, container, false)
    }

}