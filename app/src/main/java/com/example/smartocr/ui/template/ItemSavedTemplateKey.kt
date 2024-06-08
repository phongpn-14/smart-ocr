package com.example.smartocr.ui.template

import android.graphics.Color
import android.view.View
import com.example.smartocr.data.model.TemplateKey
import com.example.smartocr.util.TypeAction
import com.example.smartocr.util.dp
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.LayoutItemSavedTemplateBinding
import com.xwray.groupie.viewbinding.BindableItem

data class ItemTemplateState(val template: TemplateKey, val isSelected: Boolean = false)

class ItemSavedTemplate(val templateState: ItemTemplateState, val onclick: TypeAction<TemplateKey>) :
    BindableItem<LayoutItemSavedTemplateBinding>() {
    override fun bind(p0: LayoutItemSavedTemplateBinding, p1: Int) {
        p0.apply {
            root.setOnClickListener {
                onclick.invoke(templateState.template)
            }

            tvName.text = templateState.template.document.name
            root.setCardBackgroundColor(if (templateState.isSelected) Color.parseColor("#DBDBFF") else Color.TRANSPARENT)
            root.strokeWidth = if (templateState.isSelected) 0.dp else 1.dp
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_item_saved_template
    }

    override fun initializeViewBinding(p0: View): LayoutItemSavedTemplateBinding {
        return LayoutItemSavedTemplateBinding.bind(p0)
    }
}