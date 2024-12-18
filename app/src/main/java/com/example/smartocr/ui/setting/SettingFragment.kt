package com.example.smartocr.ui.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.smartocr.base.BaseFragment
import com.example.smartocr.data.drive.GoogleDriveServiceHelper
import com.example.smartocr.ui.auth.AuthViewModel
import com.example.smartocr.util.logd
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.proxglobal.smart_ocr.R
import com.proxglobal.smart_ocr.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var drive: Drive? = null
    private val oauthCredential by lazy {
        GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf(DriveScopes.DRIVE_FILE)
        )
    }
    private var driveHelper: GoogleDriveServiceHelper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun initView() {
        super.initView()
    }

    override fun addAction() {
        super.addAction()
        binding.btSignOut.setOnClickListener {
            authViewModel.signOut()
            GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                .signOut()
            navigate(R.id.authFragment)
        }

        binding.btManageKey.setOnClickListener {
            navigate(R.id.manageTemplateKeyFragment, bundleOf("isManageKey" to true))
        }

        binding.btManageAccount.setOnClickListener {
            toastShort("Tính năng đang phát triển")
        }

        binding.root.setOnClickListener {
            hideKeyboard(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let { intent ->
                    val selectPath = createFileFromContentUri(intent.data!!).absolutePath
                    selectPath.logd()
                    toastShort("Uploading... Please wait")
                    driveHelper!!.uploadFileToGoogleDrive(selectPath)
                        .addOnSuccessListener {
                            toastShort("Success")
                            "uploadDone = ${true}".logd()
                        }.addOnFailureListener {
                            toastShort("Failure, message = ${it.message}")
                            "uploadDone = ${it.message}".logd()
                        }
                } ?: run { Log.d("SettingFragment", "onActivityResult: intent null") }
            }
        } else if (requestCode == 2) {
            oauthCredential.selectedAccount =
                GoogleSignIn.getSignedInAccountFromIntent(data).result!!.account
            drive = Drive.Builder(
                NetHttpTransport.Builder().build(),
                GsonFactory.getDefaultInstance(),
                oauthCredential
            ).setApplicationName(getString(R.string.app_name))
                .build()
            driveHelper = GoogleDriveServiceHelper(drive)
            driveHelper!!.createFolder()
        }

    }

    fun createFileFromContentUri(fileUri: Uri): File {

        var fileName: String = ""

        fileUri.let { returnUri ->
            requireActivity().contentResolver.query(returnUri, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        //  For extract file mimeType
        val fileType: String? = fileUri.let { returnUri ->
            requireActivity().contentResolver.getType(returnUri)
        }

        val iStream: InputStream = requireActivity().contentResolver.openInputStream(fileUri)!!
        val outputDir: File = context?.cacheDir!!
        val outputFile: File = File(outputDir, fileName)
        copyStreamToFile(iStream, outputFile)
        iStream.close()
        return outputFile
    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }
}