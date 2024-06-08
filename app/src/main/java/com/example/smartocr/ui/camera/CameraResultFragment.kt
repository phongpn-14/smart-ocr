package com.example.smartocr.ui.camera

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.Resource
import com.example.smartocr.util.gone
import com.example.smartocr.util.visible
import com.hjq.permissions.XXPermissions
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
            XXPermissions.with(requireContext())
                .permission(
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                .request { _, granted ->
                    if (granted) {
                        val job = cameraViewModel.scanJob
                        when (job) {
                            is CCCDJob -> processCCCD()
                            is WithoutTemplateJob -> processWithoutTemplate()
                            is TemplateJob -> processTemplate(job.templateId)
                            is TableJob -> processTable()
                        }
                    } else {
                        toastShort("Tính năng cần cấp quyền!!")
                    }
                }
        }
    }

    private fun processCCCD() {
        cameraViewModel.processPictureResult {
            withContext(Dispatchers.Main) { handleResult(it) }
        }
    }

    private fun processWithoutTemplate() {
        cameraViewModel.processWithoutTemplate {
            withContext(Dispatchers.Main) { handleResult(it) }
        }
    }

    private fun processTemplate(templateId: String) {
        cameraViewModel.processTemplate(templateId) {
            withContext(Dispatchers.Main) { handleResult(it) }
        }
    }

    private fun processTable() {
        cameraViewModel.processTable(requireContext()) {
            withContext(Dispatchers.Main) { handleResult(it) }
        }
    }

    private suspend fun handleResult(result: Resource<ScanResult>) {
        result.whenSuccess {
            dismissLoading()
            binding.btContinue.isEnabled = true
            try {
                val screen = when (it.data) {
                    is ScanResult.CCCDResult -> R.id.viewScannedCCCDFragment
                    is ScanResult.TemplateResult -> R.id.viewOcrTemplateResultFragment
                    is ScanResult.TableResult -> R.id.viewOcrTableFragment
                    else -> R.id.viewOcrSimpleFragment
                }
                findNavController().navigate(screen, bundleOf("result" to it.data), navOptions {
                    this.popUpTo(R.id.homeFragment) {
                        this.inclusive = false
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.whenError {
            dismissLoading()
            binding.btContinue.isEnabled = true
            toastLong("Ảnh không hợp lệ. Vui lòng chọn ảnh khác")
        }.whenLoading {
            showLoading("Processing...")
            binding.btContinue.isEnabled = false
            toastShort("Processing...")
        }
    }
}