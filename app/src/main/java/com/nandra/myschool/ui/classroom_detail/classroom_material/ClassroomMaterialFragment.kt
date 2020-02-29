package com.nandra.myschool.ui.classroom_detail.classroom_material

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ale.rainbowsdk.RainbowSdk
import com.nandra.myschool.R
import com.nandra.myschool.adapter.ClassroomMaterialListAdapter
import com.nandra.myschool.model.Material
import com.nandra.myschool.ui.classroom_detail.ClassroomDetailViewModel
import com.nandra.myschool.utils.Utility.DataLoadState
import com.nandra.myschool.utils.Utility.LOG_DEBUG_TAG
import kotlinx.android.synthetic.main.classroom_material_fragment.*
import java.net.URL

class ClassroomMaterialFragment : Fragment() {

    private val classroomDetailViewModel: ClassroomDetailViewModel by activityViewModels()
    private lateinit var classroomMaterialListAdapter: ClassroomMaterialListAdapter
    private val STORAGE_PERMISSION_CODE = 111
    private var currentMaterial = Material()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.classroom_material_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classroomDetailViewModel.materialDataLoadState.observe(viewLifecycleOwner, Observer {
            handleMaterialLoadState(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        classroomMaterialListAdapter = ClassroomMaterialListAdapter(::downloadClickCallback)
        fragment_classroom_material_recycler_view.apply {
            adapter = classroomMaterialListAdapter
            layoutManager = GridLayoutManager(activity, 3)
        }
    }

    private fun handleMaterialLoadState(state: DataLoadState) {
        when(state) {
            DataLoadState.UNLOADED -> {
                classroomDetailViewModel.getMaterialList()
            }
            DataLoadState.LOADING -> { }
            DataLoadState.LOADED -> {
                classroomMaterialListAdapter.submitList(classroomDetailViewModel.materialList)
                classroomMaterialListAdapter.notifyDataSetChanged()
            }
            else -> {}
        }
    }

    private fun downloadClickCallback(material: Material) {
        currentMaterial = material
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                activity?.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                downloadFileFromUrl(currentMaterial.material_download_url)
            }
        } else {
            downloadFileFromUrl(currentMaterial.material_download_url)
        }
    }

    private fun downloadFileFromUrl(stringUrl: String) {
        val request = DownloadManager.Request(Uri.parse(stringUrl))
        request.apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            setTitle("Downloading ${currentMaterial.material_name}")
            setDescription("The File Is Downloading...")
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}")

            val manager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFileFromUrl(currentMaterial.material_download_url)
                } else {
                    Toast.makeText(activity, "Permission Denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}