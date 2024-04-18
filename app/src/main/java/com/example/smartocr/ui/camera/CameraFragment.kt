package com.example.smartocr.ui.camera

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    private val cameraViewModel by viewModels<CameraViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_camera
    }

    override fun initView() {
        super.initView()
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                cameraViewModel.processPictureResult(result) {
                    withContext(Dispatchers.Main) {
                        it.whenSuccess {
                            findNavController().navigate(
                                R.id.viewScannedCCCDFragment,
                                bundleOf("cccd" to it.data!!)
                            )
                            toastShort(it.data!!.name)
                        }.whenError {
                            toastShort(it.message!!)
                        }.whenLoading {
                            toastShort("Processing...")
                        }
                    }
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