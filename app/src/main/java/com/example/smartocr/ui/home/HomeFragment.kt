package com.example.smartocr.ui.home

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.util.SharePreferenceExt
import com.example.smartocr.util.repeatOnLifecycleStartState
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

    private val homeViewModel by activityViewModels<HomeViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        super.initView()
        binding.tvUserName.text = SharePreferenceExt.username
    }

    override fun addObserver() {
        super.addObserver()
        repeatOnLifecycleStartState {
            launch {
                homeViewModel.listCCCD().collect {
                    it.whenSuccess {
                        binding.tvCccdCount.text = "${it.data!!.size} File"
                    }
                }
            }
        }
    }

    override fun addAction() {
        super.addAction()

        binding.btSavedCccd.setOnClickListener {
//            lifecycleScope.launch(Dispatchers.IO) {
//                dataRepositorySource.listCCCD().collect {
            navigate(R.id.listScannedCCCDFragment)
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return true
    }
}