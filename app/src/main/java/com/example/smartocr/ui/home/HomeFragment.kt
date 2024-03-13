package com.example.smartocr.ui.home

import com.proxglobal.smart_ocr.R
import com.example.smartocr.base.BaseFragment
import com.proxglobal.smart_ocr.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }
}