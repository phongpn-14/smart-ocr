package com.example.smartocr.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageIntentChooser
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.util.dp
import com.example.smartocr.util.logd
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    companion object {
        val mode = 1
        const val MODE_CCCD = 0
        const val MODE_TEMPLATE = 1
        const val MODE_WITHOUT_TEMPLATE = 2
        const val MODE_TABLE = 3

        var libraryUri: Uri? = null

    }

    private val cameraViewModel by activityViewModels<CameraViewModel>()
    private val cropImage = this.registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            cameraViewModel.tmpResultFile = uriFilePath?.let { File(it) }
            findNavController().navigate(R.id.cameraResultFragment)
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    private val intentChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityRes ->
            if (activityRes.resultCode == Activity.RESULT_OK) {
                /*
                    Here we don't know whether a gallery app or the camera app is selected
                    via the intent chooser. If a gallery app is selected and an image is
                    chosen then we get the result from activityRes.
                    If a camera app is selected we take the uri we passed to the camera
                    app for storing the captured image
                 */
                (activityRes.data?.data).let { uri ->
                    onPickImageResult(uri)
                }
            } else {
                setResultCancel()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        ciIntentChooser.logd()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_camera
    }

    override fun initViewModel() {
        super.initViewModel()
        cameraViewModel.scanJob = requireArguments().getParcelable("job")!!
    }

    override fun initView() {
        super.initView()
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)
        binding.cameraView.snapshotMaxHeight = 2340
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                cameraViewModel.convertResult(
                    result,
                    binding.cutOff.excludedWidth,
                    binding.cutOff.excludedHeight,
                    binding.cutOff.cutOffTopOffset,
                    requireContext()
                ) {
                    it.whenSuccess {
                        findNavController().navigate(R.id.cameraResultFragment)
                    }.whenError {
                        toastShort(it.message!!)
                    }
                }
            }
        })
        when (cameraViewModel.scanJob) {
            is TemplateJob -> {
                binding.cutOff.setCutOffSize(300.dp, 500.dp)
                binding.cutOff.setOffset(0)
            }
        }
    }

    override fun addAction() {
        super.addAction()
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btTakePhoto.setOnClickListener {
            binding.cameraView.takePictureSnapshot()
        }

        binding.slider.addOnChangeListener { _, fl, fromUser ->
            if (fromUser) {
                binding.cutOff.setCutOffSize(300.dp, (500.dp * fl).toInt())
            }
        }

        binding.btLibrary.setOnClickListener {
//            cropImage.launch(
//                options {
//                    setToolbarBackButtonColor(Color.BLACK)
//                    setToolbarColor(Color.WHITE)
//                    setActivityTitle("Crop Image")
//                    setImageSource(includeGallery = true, includeCamera = false)
//                    setGuidelines(CropImageView.Guidelines.ON)
//                    setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
//                }
//            )
            pickImage()
        }
    }

    private fun pickImage() {
       val pickIntent = Intent(Intent.ACTION_CHOOSER, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            action = Intent.ACTION_PICK
            type = "image/*"
        }
        intentChooser.launch(pickIntent)
//        ciIntentChooser.showChooserIntent(
//            includeCamera = false,
//            includeGallery = true,
//        )
    }

    private fun setResultCancel() {
        toastShort("Please choose one picture")
    }

    fun onPickImageResult(uri: Uri?) {
        libraryUri = uri
        navigate(R.id.cropImageFragment)
    }
}