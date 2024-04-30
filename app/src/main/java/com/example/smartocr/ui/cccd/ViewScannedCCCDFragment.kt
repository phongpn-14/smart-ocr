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
        binding.tvName.setText(ocrCCCD.name)
        binding.tvBirthday.setText(ocrCCCD.birth)
        binding.tvId.setText(ocrCCCD.iD)
        binding.tvSex.setText(ocrCCCD.sex)
        binding.tvHomeTown.setText(ocrCCCD.homeTown)
        binding.tvPermanentAddress.setText(ocrCCCD.permanentAddress)
        binding.tvNationality.setText(ocrCCCD.nationality)
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btSave.setOnClickListener {
            toastShort("Coming soon")
        }

        binding.root.setOnClickListener {
            hideKeyboard(true)
        }
    }

}