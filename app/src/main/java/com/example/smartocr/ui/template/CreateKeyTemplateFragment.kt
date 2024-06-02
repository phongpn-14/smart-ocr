package com.example.smartocr.ui.template

import android.graphics.Rect
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.util.dp
import com.example.smartocr.util.groupieAdapter
import com.example.smartocr.util.postValue
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCreateKeyTemplateBinding
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CreateKeyTemplateFragment : BaseFragment<FragmentCreateKeyTemplateBinding>() {
    private val templateViewModel by activityViewModels<TemplateViewModel>()
    private val keys = MutableLiveData<MutableSet<String>>(mutableSetOf())
    private val keyAdapter = groupieAdapter()

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_key_template
    }

    override fun initView() {
        super.initView()
        keys.postValue { clear() }
        binding.rvKeys.apply {
            adapter = keyAdapter
            layoutManager = FlowLayoutManager()
                .setAlignment(Alignment.LEFT)
                .maxItemsPerLine(3)
                .apply { isAutoMeasureEnabled = true }

            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.right = 8.dp
                    outRect.top = 4.dp
                }
            })
        }
    }


    override fun addAction() {
        super.addAction()

        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.edtKeys.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEND ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                toastShort(v.text.toString())
                if (v.text.toString().trim().isNotEmpty()) {
                    keys.postValue {
                        add(v.text.toString())
                    }
                }
                // Do whatever you want here
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.btSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            if (name.isBlank()) {
                toastShort("Tên key không được bỏ trống")
                return@setOnClickListener
            }

            val keys = keys.value!!.toList()
            if (keys.isEmpty()) {
                toastShort("Điền ít nhất 1 key")
                return@setOnClickListener
            }

            templateViewModel.createKey(keys, name) {
                it.whenLoading {
                    withContext(Dispatchers.Main) {
                        showLoading()
                    }}
                    .whenSuccess {
                        withContext(Dispatchers.Main) {
                            toastShort("Key update successfully!")
                        }
                    }.whenError {
                        withContext(Dispatchers.Main) {
                            toastShort(it.message!!)
                        }
                    }
            }
        }
    }

    override fun addObserver() {
        super.addObserver()
        keys.observe(viewLifecycleOwner) {
            keyAdapter.update(it.map { keyName ->
                ItemKey(keyName) {
                    keys.postValue { remove(it) }
                }
            })
        }
    }

}