package com.example.smartocr.ui.splash

import androidx.lifecycle.lifecycleScope
import com.example.smartocr.base.BaseFragment
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment: BaseFragment<FragmentSplashBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

    override fun initView() {
        super.initView()
        lifecycleScope.launch {
            showLoading()
            delay(2000)
            navigate(R.id.authFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissLoading()
    }
}