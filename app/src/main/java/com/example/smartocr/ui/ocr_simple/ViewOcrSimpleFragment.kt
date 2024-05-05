package com.example.smartocr.ui.ocr_simple

import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.ScanResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentViewOcrSimpleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewOcrSimpleFragment : BaseFragment<FragmentViewOcrSimpleBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_view_ocr_simple
    }

    override fun initView() {
        super.initView()
        val result = requireArguments().getParcelable<ScanResult.SimpleResult>("result")
        binding.tvResult.text = result?.result ?: "null"
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}