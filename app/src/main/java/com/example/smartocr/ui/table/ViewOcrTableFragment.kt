package com.example.smartocr.ui.table

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.ScanResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentViewOcrTableBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ViewOcrTableFragment : BaseFragment<FragmentViewOcrTableBinding>() {
    private lateinit var file: File
    override fun getLayoutId(): Int {
        return R.layout.fragment_view_ocr_table
    }

    override fun initView() {
        super.initView()
        val result = requireArguments().getParcelable<ScanResult.TableResult>("result")
        file = File(result!!.fileUrl)
        binding.tvFileName.text = file.name
        binding.tvFileName.isSelected  = true
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
            val createChooser = Intent(Intent.ACTION_VIEW)
            createChooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            createChooser.setDataAndType(uri, "application/vnd.ms-excel")
            requireContext().startActivity(createChooser)
        }

        binding.btUpload.setOnClickListener {
            upFileToGoogleDrive(file)
        }

        binding.btShare.setOnClickListener {
            shareMedia(requireContext(), file)
        }
    }

    private fun shareMedia(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context, context.packageName + ".provider", file
            )
            try {
                val intent =
                    ShareCompat.IntentBuilder(requireContext())
                        .setType(context.contentResolver.getType(uri))
                        .setStream(uri).intent
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val createChooser = Intent.createChooser(intent, "Share File")
                createChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(createChooser)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

}