package com.example.smartocr.ui.template

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.CameraFragment
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentSavedTemplateBinding

class SavedTemplateFragment : BaseFragment<FragmentSavedTemplateBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_saved_template
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btContinue.setOnClickListener {
            findNavController().navigate(
                R.id.cameraFragment,
                bundleOf("mode" to CameraFragment.MODE_TEMPLATE)
            )
        }
    }
}