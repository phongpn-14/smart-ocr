package com.example.smartocr.ui.camera

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.util.toFile
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCropImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropImageFragment : BaseFragment<FragmentCropImageBinding>() {
    private val cameraViewModel by activityViewModels<CameraViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_crop_image
    }

    override fun initView() {
        super.initView()
        binding.cropImageView.setImageUriAsync(CameraFragment.libraryUri)
    }

    override fun addAction() {
        super.addAction()
        binding.btContinue.setOnClickListener {
            cameraViewModel.tmpResultBitmap = binding.cropImageView.croppedImage
            cameraViewModel.tmpResultFile = binding.cropImageView.croppedImage?.toFile()
            findNavController().navigate(R.id.cameraResultFragment)
        }

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}