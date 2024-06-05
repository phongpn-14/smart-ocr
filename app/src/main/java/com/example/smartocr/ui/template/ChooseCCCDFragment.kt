package com.example.smartocr.ui.template

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.cccd.ItemScannedCCCD
import com.example.smartocr.ui.home.HomeViewModel
import com.example.smartocr.util.groupieAdapter
import com.example.smartocr.util.repeatOnLifecycleStartState
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentAutoFillViewCccdBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseCCCDFragment : BaseFragment<FragmentAutoFillViewCccdBinding>() {
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val scannedCCCDAdapter = groupieAdapter()

    override fun getLayoutId(): Int {
        return R.layout.fragment_auto_fill_view_cccd
    }

    override fun initView() {
        super.initView()
        binding.rvScannedCccd.apply {
            adapter = scannedCCCDAdapter
            itemAnimator = null
        }
    }

    override fun addObserver() {
        super.addObserver()
        repeatOnLifecycleStartState {
            launch {
                homeViewModel.listCCCD().collect {
                    it.whenSuccess {
                        scannedCCCDAdapter.update(it.data!!.map {
                            ItemScannedCCCD(it) { cccd ->
                                navigate(R.id.autoFillViewDetailCCCDFragment,
                                    bundleOf("result" to cccd)
                                )
                            }
                        })
                    }
                }
            }
        }
    }
}