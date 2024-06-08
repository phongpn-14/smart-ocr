package com.example.smartocr.ui.dialog

import com.example.smartocr.base.BaseDialog
import com.example.smartocr.util.Action
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.DialogDeleteBinding

class DialogDelete : BaseDialog<DialogDeleteBinding>() {

    private var onDelete: Action? = null
    override fun getLayoutId(): Int {
        return R.layout.dialog_delete
    }

    override fun addAction() {
        super.addAction()
        binding.btDelete.setOnClickListener {
            onDelete?.invoke()
            dismiss()
        }

        binding.btCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(onDelete: Action) = DialogDelete().apply {
            this.onDelete = onDelete
        }
    }
}