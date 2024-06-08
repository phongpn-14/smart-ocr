package com.example.smartocr.ui.document

import android.view.View
import com.example.smartocr.data.model.FileDocument
import com.example.smartocr.util.TypeAction
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.LayoutItemFileDocumentBinding
import com.xwray.groupie.viewbinding.BindableItem

class ItemFileResult(
    private val document: FileDocument,
    private val onClick: TypeAction<FileDocument>,
    private val onDelete: TypeAction<FileDocument>
) :
    BindableItem<LayoutItemFileDocumentBinding>() {
    override fun bind(viewBinding: LayoutItemFileDocumentBinding, position: Int) {
        viewBinding.apply {
            tvFileName.text = document.document.filePath.split("/").last()
            try {
                tvFileCreateDate.text = document.document.createdDate.split(" ").first()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (document.document.filePath.contains(".docx")) {
                ivFileThumb.setImageResource(R.drawable.ic_export_file_docx)
            } else if (document.document.filePath.contains(".xlsx")) {
                ivFileThumb.setImageResource(R.drawable.ic_export_file_xlsx)
            }

            root.setOnClickListener { onClick.invoke(document) }
            btDelete.setOnClickListener { onDelete.invoke(document) }
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_item_file_document
    }

    override fun initializeViewBinding(view: View): LayoutItemFileDocumentBinding {
        return LayoutItemFileDocumentBinding.bind(view)
    }
}