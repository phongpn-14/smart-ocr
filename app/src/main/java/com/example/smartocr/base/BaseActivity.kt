package com.example.smartocr.base

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartocr.data.drive.GoogleDriveServiceHelper
import com.example.smartocr.util.logd
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: T
    private var hud: KProgressHUD? = null

    private var drive: Drive? = null
    private val oauthCredential by lazy {
        GoogleAccountCredential.usingOAuth2(this, listOf(DriveScopes.DRIVE_FILE))
    }
    private var driveHelper: GoogleDriveServiceHelper? = null
    private val REQ_CODE_DRIVE = 8888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.executePendingBindings()
        initViewModel()
        initView()
        addObserver()
        addAction()
        initData()
    }

    protected open fun initViewModel() {}

    protected open fun addObserver() {}

    protected abstract fun getLayoutId(): Int
    protected open fun initView() {}
    protected open fun addAction() {}
    protected open fun initData() {}

    protected open fun isActivityFullscreen() = false
    protected open fun getFragmentContainer(): Int {
        return R.id.content
    }

    @SuppressLint("CommitTransaction")
    fun showChildFragment(child: Fragment, name: String) {
        supportFragmentManager.beginTransaction()
            .replace(getFragmentContainer(), child, name)
            .commit()
    }

    fun toastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun hideKeyboard(clearFocusView: Boolean = true) {
        var viewFocus = currentFocus
        if (viewFocus == null) {
            viewFocus = findViewById(android.R.id.content) ?: return
        }

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(viewFocus.windowToken, 0)
        if (clearFocusView) {
            viewFocus.clearFocus()
        }
    }

    fun showLoading() {
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
            .show();
    }

    fun dismissLoading() {
        hud?.dismiss()
        hud = null
    }

    fun upFileToGoogleDrive(file: File) {
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            oauthCredential.selectedAccount = it.account
            drive = Drive.Builder(
                NetHttpTransport.Builder().build(),
                GsonFactory.getDefaultInstance(),
                oauthCredential
            ).setApplicationName(getString(com.proxglobal.smart_ocr.R.string.app_name))
                .build()
            driveHelper = GoogleDriveServiceHelper(drive)
            showLoading()
            lifecycleScope.launch(Dispatchers.Default) {
                val result = driveHelper!!.isFolderPresent.await()
                if (result.isNotBlank()) {
                    driveHelper!!.folderId = result
                    delay(1000)
                    driveHelper!!.uploadFileToGoogleDrive(file.absolutePath)
                        .addOnSuccessListener {
                            lifecycleScope.launch {
                                dismissLoading()
                                toastShort("Success")
                            }
                            "uploadDone = ${true}".logd()
                        }.addOnFailureListener {
                            lifecycleScope.launch {
                                dismissLoading()
                                toastShort("Failure, message = ${it.message}")
                            }
                            "uploadDone = ${it.message}, file = ${file.absolutePath}".logd()
                        }
                } else driveHelper!!.createFolder().addOnCompleteListener {
                    "folderId = ${it.result}, success = ${it.isSuccessful}".logd()
                    driveHelper!!.folderId = it.result
                    lifecycleScope.launch {
                        delay(1000)
                        driveHelper!!.uploadFileToGoogleDrive(file.absolutePath)
                            .addOnSuccessListener {
                                lifecycleScope.launch {
                                    dismissLoading()
                                    toastShort("Success")
                                }
                                "uploadDone = ${true}".logd()
                            }.addOnFailureListener {
                                lifecycleScope.launch {
                                    dismissLoading()
                                    toastShort("Failure, message = ${it.message}")
                                }
                                "uploadDone = ${it.message}, file = ${file.absolutePath}".logd()
                            }
                    }
                }
            }
        } ?: run {
            val client = GoogleSignIn.getClient(
                this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .requestEmail()
                    .build()
            )
            startActivityForResult(client.signInIntent, REQ_CODE_DRIVE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_DRIVE) {
            if (resultCode == Activity.RESULT_OK) {

                //Create folder in Google Drive
                oauthCredential.selectedAccount =
                    GoogleSignIn.getSignedInAccountFromIntent(data).result!!.account
                drive = Drive.Builder(
                    NetHttpTransport.Builder().build(),
                    GsonFactory.getDefaultInstance(),
                    oauthCredential
                ).setApplicationName(getString(com.proxglobal.smart_ocr.R.string.app_name))
                    .build()
                driveHelper = GoogleDriveServiceHelper(drive)
                driveHelper!!.createFolder()
            }

        }
    }
}