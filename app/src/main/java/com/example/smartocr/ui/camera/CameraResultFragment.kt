package com.example.smartocr.ui.camera

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.smartocr.base.BaseFragment
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCameraResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CameraResultFragment : BaseFragment<FragmentCameraResultBinding>() {
    private val cameraViewModel by activityViewModels<CameraViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_camera_result
    }

    override fun initView() {
        super.initView()
        binding.ivResult.setImageBitmap(cameraViewModel.getTempResult())
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            cameraViewModel.retake()
            findNavController().navigateUp()
        }

        binding.btContinue.setOnClickListener {
            when (cameraViewModel.mode) {
                CameraFragment.MODE_CCCD -> processCCCD()
            }

        }
    }

    private fun processCCCD() {
        cameraViewModel.processPictureResult {
            withContext(Dispatchers.Main) {
                it.whenSuccess {
                    try {
                        findNavController().navigate(
                            R.id.viewScannedCCCDFragment,
                            bundleOf("cccd" to it.data!!),
                            navOptions {
                                this.popUpTo(R.id.cameraFragment) {
                                    this.inclusive = false
                                }
                            }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.whenError {
                    toastShort(it.message!!)
                }.whenLoading {
                    toastShort("Processing...")
                }
            }
        }
    }

    private fun processWithoutTemplate() {
        cameraViewModel.processWithoutTemplate {
            withContext(Dispatchers.Main) {
                it.whenSuccess {
                    try {
                        findNavController().navigate(
                            R.id.viewScannedCCCDFragment,
                            bundleOf("cccd" to it.data!!),
                            navOptions {
                                this.popUpTo(R.id.cameraFragment) {
                                    this.inclusive = false
                                }
                            }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.whenError {
                    toastShort(it.message!!)
                }.whenLoading {
                    toastShort("Processing...")
                }
            }
        }
    }
}