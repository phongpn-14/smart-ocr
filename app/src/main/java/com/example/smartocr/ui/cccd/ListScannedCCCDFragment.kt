package com.example.smartocr.ui.cccd

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.ScanResult
import com.example.smartocr.ui.home.HomeViewModel
import com.example.smartocr.util.groupieAdapter
import com.example.smartocr.util.repeatOnLifecycleStartState
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentListScannedCccdBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListScannedCCCDFragment : BaseFragment<FragmentListScannedCccdBinding>() {
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val scannedCCCDAdapter = groupieAdapter()

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_scanned_cccd
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
                                navigate(
                                    R.id.viewScannedCCCDFragment,
                                    bundleOf(
                                        "result" to ScanResult.CCCDResult(ocrCCCD = cccd),
                                        "deletable" to true,
                                        "editable" to true
                                    )
                                )
                            }
                        })
                    }
                }
            }
        }
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}