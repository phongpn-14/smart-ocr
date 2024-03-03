package com.proxglobal.smart_ocr.ui.camera

import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.base.BaseFragment
import com.proxglobal.smart_ocr.databinding.FragmentCameraBinding

class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_camera
    }

    override fun initView() {
        super.initView()
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)
    }
}