package com.example.smartocr.ui.document

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.dialog.DialogDelete
import com.example.smartocr.util.groupieAdapter
import com.example.smartocr.util.repeatOnLifecycleStartState
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentFileResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class FragmentFileResult : BaseFragment<FragmentFileResultBinding>() {
    private val documentViewModel by activityViewModels<DocumentViewModel>()
    private val fileResultAdapter = groupieAdapter()
    override fun getLayoutId(): Int {
        return R.layout.fragment_file_result
    }

    override fun initView() {
        super.initView()
        binding.rvFileResult.apply {
            adapter = fileResultAdapter
            itemAnimator = null
        }
    }

    override fun addObserver() {
        super.addObserver()
        repeatOnLifecycleStartState {
            launch {
                documentViewModel.listFileResult().collect {
                    it.whenSuccess {
                        fileResultAdapter.update(it.data!!.map {
                            ItemFileResult(it, onClick = {
                                documentViewModel.downloadFile(
                                    requireContext(),
                                    it.document.filePath,
                                    if (it.document.filePath.contains(".docx")) "docx" else "xlsx"
                                ) {
                                    val file = File(it)
                                    val uri = FileProvider.getUriForFile(
                                        requireContext(),
                                        requireContext().packageName + ".provider",
                                        file
                                    )
                                    val createChooser = Intent(Intent.ACTION_VIEW)
                                    createChooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    val mimeType = MimeTypeMap.getSingleton()
                                        .getMimeTypeFromExtension(
                                            MimeTypeMap.getFileExtensionFromUrl(
                                                file.absolutePath
                                            )
                                        );
                                    createChooser.setDataAndType(uri, mimeType)
                                    requireContext().startActivity(createChooser)
                                }
                            }, onDelete = {
                                DialogDelete.newInstance {
                                    documentViewModel.deleteFileResult(it.id) {
                                        withContext(Dispatchers.Main) {
                                            toastShort("Delete successfully!")
                                        }
                                    }
                                }.show(childFragmentManager, null)
                            })
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