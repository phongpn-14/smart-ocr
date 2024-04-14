package com.example.smartocr.ui.home

import androidx.lifecycle.lifecycleScope
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.DataRepositorySource
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    @Inject
    lateinit var dataRepositorySource: DataRepositorySource
    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun addAction() {
        super.addAction()
        binding.btHello.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                dataRepositorySource.helloWorld().collect {
                    withContext(Dispatchers.Main) {
                        it.whenSuccess { toastShort("Hello world from server") }
                            .whenError { resource -> toastShort(resource.message!!) }
                    }
                }
            }
        }
    }
}