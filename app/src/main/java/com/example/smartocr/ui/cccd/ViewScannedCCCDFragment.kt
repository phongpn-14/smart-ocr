package com.example.smartocr.ui.cccd

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.ui.DocumentViewModel
import com.example.smartocr.ui.camera.ScanResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentViewScannedCccdBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewScannedCCCDFragment : BaseFragment<FragmentViewScannedCccdBinding>() {
    private lateinit var ocrCCCD: OcrCCCD
    private var editable: Boolean = true
    private val documentViewModel by activityViewModels<DocumentViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_view_scanned_cccd
    }

    override fun initView() {
        super.initView()
        editable = requireArguments().getBoolean("editable", editable)
        try {
            val result = requireArguments().getParcelable<ScanResult.CCCDResult>("result")!!
            ocrCCCD = result.ocrCCCD.copy()
            binding.tvName.setText(ocrCCCD.name)
            binding.tvBirthday.setText(ocrCCCD.birth)
            binding.tvId.setText(ocrCCCD.iD)
            binding.tvSex.setText(ocrCCCD.sex)
            binding.tvHomeTown.setText(ocrCCCD.homeTown)
            binding.tvPermanentAddress.setText(ocrCCCD.permanentAddress)
            binding.tvNationality.setText(ocrCCCD.nationality)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.btSave.isVisible = editable
        binding.tvName.isEnabled = editable
        binding.tvBirthday.isEnabled = editable
        binding.tvId.isEnabled = editable
        binding.tvSex.isEnabled = editable
        binding.tvHomeTown.isEnabled = editable
        binding.tvPermanentAddress.isEnabled = editable
        binding.tvNationality.isEnabled = editable
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

        binding.tvNationality.addTextChangedListener {
            ocrCCCD.nationality = it.toString().trim()
        }

        binding.tvBirthday.addTextChangedListener {
            ocrCCCD.birth = it.toString().trim()
        }

        binding.tvId.addTextChangedListener {
            ocrCCCD.iD = it.toString().trim()
        }

        binding.tvSex.addTextChangedListener {
            ocrCCCD.sex = it.toString().trim()
        }

        binding.tvHomeTown.addTextChangedListener {
            ocrCCCD.homeTown = it.toString().trim()
        }

        binding.tvPermanentAddress.addTextChangedListener {
            ocrCCCD.permanentAddress = it.toString().trim()
        }

        binding.tvName.addTextChangedListener {
            ocrCCCD.name = it.toString().trim()
        }

        binding.btSave.setOnClickListener {
            documentViewModel.editCCCD(ocrCCCD) {
                lifecycleScope.launch {
                    it.whenLoading { toastShort("Process") }
                        .whenSuccess {
                            toastShort(it.data!!)
                            navigate(
                                R.id.homeFragment,
                                popUpTo = R.id.homeFragment,
                                popUpToBuilder = {
                                    inclusive = false
                                })
                        }
                        .whenError { toastShort(it.message ?: "Unknown error") }
                }
            }
        }
    }

}