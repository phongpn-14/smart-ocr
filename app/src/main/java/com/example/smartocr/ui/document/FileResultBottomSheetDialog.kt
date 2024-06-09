package com.example.smartocr.ui.document

import com.example.smartocr.base.BaseBottomSheetDialog
import com.example.smartocr.data.model.FileDocument
import com.example.smartocr.util.TypeAction
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.DialogBottomSheetFileOptionBinding

class FileResultBottomSheetDialog(
    private val fileResult: FileDocument,
    private val onUpload: TypeAction<FileDocument>,
    private val onDelete: TypeAction<FileDocument>,
    private val onShare: TypeAction<FileDocument>
) :
    BaseBottomSheetDialog<DialogBottomSheetFileOptionBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_sheet_file_option
    }

    override fun initView() {
        super.initView()
        binding.tvTitle.text = fileResult.document.filePath.split("/").last()
    }

    override fun addAction() {
        super.addAction()
        binding.btShare.setOnClickListener {
            onShare.invoke(fileResult)
            dismiss()
        }

        binding.btDelete.setOnClickListener {
            onDelete.invoke(fileResult)
            dismiss()
        }

        binding.btUpload.setOnClickListener {
            onUpload.invoke(fileResult)
            dismiss()
        }
    }
}