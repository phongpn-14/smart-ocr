package com.example.smartocr.ui.cccd

import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.model.OcrCCCD
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentViewScannedCccdBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewScannedCCCDFragment : BaseFragment<FragmentViewScannedCccdBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_view_scanned_cccd
    }

    override fun initView() {
        super.initView()
        val ocrCCCD = requireArguments().getParcelable<OcrCCCD>("cccd")!!
        binding.tvName.text = ocrCCCD.name
        binding.tvBirthday.text = ocrCCCD.birth
        binding.tvId.text = ocrCCCD.iD
        binding.tvSex.text = ocrCCCD.sex
        binding.tvHomeTown.text = ocrCCCD.homeTown
        binding.tvPermanentAddress.text = ocrCCCD.permanentAddress
        binding.tvNationality.text = ocrCCCD.nationality
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}