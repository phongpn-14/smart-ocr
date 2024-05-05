package com.example.smartocr.ui.template

import android.view.View
import com.example.smartocr.util.TypeAction
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.LayoutItemSavedTemplateBinding
import com.xwray.groupie.viewbinding.BindableItem

class ItemSavedTemplate(val text: String, val onclick: TypeAction<String>) :
    BindableItem<LayoutItemSavedTemplateBinding>() {
    override fun bind(p0: LayoutItemSavedTemplateBinding, p1: Int) {
        p0.apply {
            root.setOnClickListener {
                onclick.invoke(text)
            }

            tvName.text = text
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_item_saved_template
    }

    override fun initializeViewBinding(p0: View): LayoutItemSavedTemplateBinding {
        return LayoutItemSavedTemplateBinding.bind(p0)
    }
}