package com.example.smartocr.ui.template

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.TemplateJob
import com.example.smartocr.util.groupieAdapter
import com.example.smartocr.util.repeatOnLifecycleStartState
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentSavedTemplateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedTemplateFragment : BaseFragment<FragmentSavedTemplateBinding>() {
    private val templateViewModel by activityViewModels<TemplateViewModel>()
    private val templateAdapter = groupieAdapter()
    private var selectedId: String = "1"
    override fun getLayoutId(): Int {
        return R.layout.fragment_saved_template
    }

    override fun initView() {
        super.initView()
        binding.rvSavedTemplate.apply {
            itemAnimator = null
            adapter = templateAdapter
        }
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btContinue.setOnClickListener {
            findNavController().navigate(
                R.id.cameraFragment,
                bundleOf("job" to TemplateJob(templateId = selectedId))
            )
        }
    }

    override fun addObserver() {
        super.addObserver()
        repeatOnLifecycleStartState {
            launch {
                templateViewModel.listTemplateState.collect {
                    it.whenSuccess {
                        templateAdapter.update(it.data!!.map {
                            ItemSavedTemplate(it) {


                            }
                        })
                    }

                }
                templateViewModel.templateId.collect {

                }
            }
        }
    }
}