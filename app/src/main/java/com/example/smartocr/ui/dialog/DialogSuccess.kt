package com.example.smartocr.ui.dialog

import com.example.smartocr.base.BaseDialog
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.DialogSuccessBinding

class DialogSuccess(private val title: String = "Bạn đã tải lên thành công") : BaseDialog<DialogSuccessBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.dialog_success
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener { dismiss() }
    }
}