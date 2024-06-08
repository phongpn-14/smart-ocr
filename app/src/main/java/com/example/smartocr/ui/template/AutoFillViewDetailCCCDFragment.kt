package com.example.smartocr.ui.template

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.model.OcrCCCD
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentAutoFillViewScannedCccdBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AutoFillViewDetailCCCDFragment : BaseFragment<FragmentAutoFillViewScannedCccdBinding>() {
    private var editable: Boolean = true
    private lateinit var ocrCCCD: OcrCCCD
    private val templateViewModel by activityViewModels<TemplateViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_auto_fill_view_scanned_cccd
    }


    override fun initView() {
        super.initView()
        editable = requireArguments().getBoolean("editable", editable)
        try {
            ocrCCCD = requireArguments().getParcelable<OcrCCCD>("result")!!
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
            templateViewModel.autoFill(ocrCCCD.objectID) {
                withContext(Dispatchers.Main) {
                    it.whenLoading {
                        withContext(Dispatchers.Main) { showLoading() }
                    }.whenSuccess {
                        navigate(
                            R.id.viewOcrTemplateResultFragment,
                            bundleOf("result" to it.data, "can_autofill" to false),
                            popUpTo = R.id.cameraFragment,
                            popUpToBuilder = { inclusive = false })

                    }.whenError {
                        withContext(Dispatchers.Main) { toastShort(it.message!!) }
                    }
                }
            }
        }
    }
}