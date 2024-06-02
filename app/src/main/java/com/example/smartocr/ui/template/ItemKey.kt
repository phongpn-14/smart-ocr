package com.example.smartocr.ui.template

import android.view.View
import com.example.smartocr.util.TypeAction
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.LayoutItemCreateKeyTemplateBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class ItemKey(private val name: String, private val onClick: TypeAction<String>) :
    BindableItem<LayoutItemCreateKeyTemplateBinding>() {
    override fun bind(binding: LayoutItemCreateKeyTemplateBinding, p1: Int) {
        binding.tvName.text = name
        binding.btClose.setOnClickListener {
            onClick.invoke(name)
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_item_create_key_template
    }

    override fun initializeViewBinding(p0: View): LayoutItemCreateKeyTemplateBinding {
        return LayoutItemCreateKeyTemplateBinding.bind(p0)
    }

    override fun isSameAs(other: Item<*>): Boolean {
        if (other is ItemKey) {
            return name == other.name
        }
        return super.isSameAs(other)
    }
}