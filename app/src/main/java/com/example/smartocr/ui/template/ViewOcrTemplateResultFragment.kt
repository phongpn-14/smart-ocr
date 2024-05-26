package com.example.smartocr.ui.template

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.ScanResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentViewOcrTableBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ViewOcrTemplateResultFragment : BaseFragment<FragmentViewOcrTableBinding>() {
    private lateinit var file: File
    override fun getLayoutId(): Int {
        return R.layout.fragment_view_ocr_table
    }

    override fun initView() {
        super.initView()
        val result = requireArguments().getParcelable<ScanResult.TemplateResult>("result")
        file = File(result!!.fileUrl)
        binding.tvFileName.text = file.name
        binding.ivFileSize.text = (file.length() / 1024).toString() + "KB"
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btResult.setOnClickListener {
            val uri = FileProvider.getUriForFile(
                requireContext(), requireContext().packageName + ".provider", file
            )
//            val intent =
//                ShareCompat.IntentBuilder(requireContext())
//                    .setType(requireContext().contentResolver.getType(uri))
//                    .setStream(uri).intent
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val createChooser = Intent(Intent.ACTION_VIEW)
            createChooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.absolutePath));
            createChooser.setDataAndType(uri, mimeType)
//            createChooser.setData(uri)
//            createChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            requireContext().startActivity(createChooser)
        }


    }
}