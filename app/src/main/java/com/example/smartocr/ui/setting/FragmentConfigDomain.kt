package com.example.smartocr.ui.setting

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.remote.baseurl
import com.example.smartocr.util.SharePreferenceExt
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentConfigDomainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FragmentConfigDomain : BaseFragment<FragmentConfigDomainBinding>() {
    @Inject
    lateinit var dataRepositorySource: DataRepositorySource
    override fun getLayoutId(): Int {
        return R.layout.fragment_config_domain
    }

    override fun initView() {
        super.initView()
        binding.edtIpServer.setText(SharePreferenceExt.lastDomain)
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.edtIpServer.addTextChangedListener {
            SharePreferenceExt.lastDomain = it.toString().trim()
            baseurl = "http://${it.toString().trim()}:3502/"
        }


        binding.btServer.setOnClickListener {
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