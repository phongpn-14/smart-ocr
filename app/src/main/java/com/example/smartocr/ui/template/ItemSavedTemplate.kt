package com.example.smartocr.ui.template

import android.view.View
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.util.TypeAction
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.LayoutItemSavedTemplateBinding
import com.xwray.groupie.viewbinding.BindableItem

data class ItemTemplateState(val template: Template, val isSelected: Boolean = false)

class ItemSavedTemplate(val templateState: ItemTemplateState, val onclick: TypeAction<Template>) :
    BindableItem<LayoutItemSavedTemplateBinding>() {
    override fun bind(p0: LayoutItemSavedTemplateBinding, p1: Int) {
        p0.apply {
            root.setOnClickListener {
                onclick.invoke(templateState.template)
            }

            tvName.text = templateState.template.id
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_item_saved_template
    }

    override fun initializeViewBinding(p0: View): LayoutItemSavedTemplateBinding {
        return LayoutItemSavedTemplateBinding.bind(p0)
    }
}