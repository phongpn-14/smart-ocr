package com.example.smartocr.ui.home

import androidx.core.os.bundleOf
import com.example.smartocr.base.BaseDialog
import com.example.smartocr.ui.camera.CCCDJob
import com.example.smartocr.ui.camera.CameraFragment
import com.example.smartocr.ui.camera.TableJob
import com.example.smartocr.ui.camera.WithoutTemplateJob
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.DialogScanOptionBinding

class ScanOptionDialog : BaseDialog<DialogScanOptionBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.dialog_scan_option
    }

    override fun addAction() {
        super.addAction()
        binding.btScanCccd.setOnClickListener {
            toCamera(CameraFragment.MODE_CCCD)
        }

        binding.btScanTemplate.setOnClickListener {
            toCamera(CameraFragment.MODE_TEMPLATE)

        }

        binding.btScanWithoutTemplate.setOnClickListener {
            toCamera(CameraFragment.MODE_WITHOUT_TEMPLATE)
        }

        binding.btScanTable.setOnClickListener {
            toCamera(CameraFragment.MODE_TABLE)
        }
    }


    private fun toCamera(mode: Int) {
        if (mode == CameraFragment.MODE_TEMPLATE) {
            navigate(R.id.savedTemplateFragment)
        } else {
            val job = when (mode) {
                CameraFragment.MODE_CCCD -> CCCDJob()
                CameraFragment.MODE_TABLE -> TableJob()
                CameraFragment.MODE_WITHOUT_TEMPLATE -> WithoutTemplateJob()
                else -> WithoutTemplateJob()
            }

            navigate(R.id.cameraFragment, bundleOf("job" to job))
        }
        dismiss()
    }
}