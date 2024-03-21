package com.example.smartocr.ui.camera

import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.proxglobal.smart_ocr.R
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

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btTakePhoto.setOnClickListener {
            toastShort("Coming soon.")
        }
    }
}