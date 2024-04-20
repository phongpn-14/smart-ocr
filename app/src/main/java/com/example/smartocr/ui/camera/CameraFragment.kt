package com.example.smartocr.ui.camera

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    private val cameraViewModel by activityViewModels<CameraViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_camera
    }

    override fun initView() {
        super.initView()
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                cameraViewModel.convertResult(result) {
                    findNavController().navigate(R.id.cameraResultFragment)
                }
            }
        })
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btTakePhoto.setOnClickListener {
            binding.cameraView.takePictureSnapshot()
        }
    }
}