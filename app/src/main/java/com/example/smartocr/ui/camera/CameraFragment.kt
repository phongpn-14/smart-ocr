package com.example.smartocr.ui.camera

import com.proxglobal.smart_ocr.R
import com.example.smartocr.base.BaseFragment
import com.proxglobal.smart_ocr.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_camera
    }

    override fun initView() {
        super.initView()
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)
    }
}