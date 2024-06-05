package com.example.smartocr.ui.cccd

import android.view.View
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.util.TypeAction
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.LayoutItemScannedCccdBinding
import com.xwray.groupie.viewbinding.BindableItem

class ItemScannedCCCD(private val cccd: OcrCCCD, private val onClick: TypeAction<OcrCCCD>) :
    BindableItem<LayoutItemScannedCccdBinding>() {
    override fun bind(p0: LayoutItemScannedCccdBinding, p1: Int) {
        p0.apply {
            tvName.text = cccd.name
            tvNumber.text = "No: ${cccd.iD}"

            root.setOnClickListener {
                onClick.invoke(cccd)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_item_scanned_cccd
    }

    override fun initializeViewBinding(p0: View): LayoutItemScannedCccdBinding {
        return LayoutItemScannedCccdBinding.bind(p0)
    }
}