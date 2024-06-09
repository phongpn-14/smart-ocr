package com.example.smartocr.ui.ocr_simple

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.ui.camera.ScanResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentViewOcrSimpleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewOcrSimpleFragment : BaseFragment<FragmentViewOcrSimpleBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_view_ocr_simple
    }

    override fun initView() {
        super.initView()
        val result = requireArguments().getParcelable<ScanResult.SimpleResult>("result")
        binding.tvResult.setText(result?.result ?: "null")
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btCopy.setOnClickListener {
            copyTextToClipboard(binding.tvResult.text.toString())
        }

        binding.btShare.setOnClickListener {
            shareText(requireContext(), binding.tvResult.text.toString())
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            "label",
            text
        )
        clipboardManager.setPrimaryClip(clip)
    }

    private fun shareText(context: Context, text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, null))
    }


}