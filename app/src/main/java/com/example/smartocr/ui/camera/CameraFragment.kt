package com.example.smartocr.ui.camera

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.util.dp
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    companion object {
        val mode = 1
        const val MODE_CCCD = 0
        const val MODE_TEMPLATE = 1
        const val MODE_WITHOUT_TEMPLATE = 2
        const val MODE_TABLE = 3

    }

    private val cameraViewModel by activityViewModels<CameraViewModel>()
    private val cropImage = this.registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            cameraViewModel.tmpResultFile = uriFilePath?.let { File(it) }
            findNavController().navigate(R.id.cameraResultFragment)
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_camera
    }

    override fun initViewModel() {
        super.initViewModel()
        cameraViewModel.scanJob = requireArguments().getParcelable("job")!!
    }

    override fun initView() {
        super.initView()
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)
        binding.cameraView.snapshotMaxHeight = 2340
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                cameraViewModel.convertResult(
                    result,
                    binding.cutOff.excludedWidth,
                    binding.cutOff.excludedHeight,
                    binding.cutOff.cutOffTopOffset,
                    requireContext()
                ) {
                    it.whenSuccess {
                        findNavController().navigate(R.id.cameraResultFragment)
                    }.whenError {
                        toastShort(it.message!!)
                    }
                }
            }
        })
        when (cameraViewModel.scanJob) {
            is TemplateJob -> {
                binding.cutOff.setCutOffSize(300.dp, 500.dp)
                binding.cutOff.setOffset(0)
            }
        }
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btTakePhoto.setOnClickListener {
            binding.cameraView.takePictureSnapshot()
        }

        binding.slider.addOnChangeListener { _, fl, fromUser ->
            if (fromUser) {
                binding.cutOff.setCutOffSize(300.dp, (500.dp * fl).toInt())
            }
        }

        binding.btLibrary.setOnClickListener {
            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                }
            )
        }
    }
}