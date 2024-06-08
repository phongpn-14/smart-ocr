package com.example.smartocr.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.remote.baseurl
import com.example.smartocr.util.OkDownloaderManager
import com.example.smartocr.util.logd
import com.example.smartocr.util.sTypeAction
import com.example.smartocr.util.toFile
import com.hjq.permissions.XXPermissions
import com.otaliastudios.cameraview.PictureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {
    var tmpResultBitmap: Bitmap? = null
    var tmpResultFile: File? = null
    lateinit var scanJob: ScanJob

    fun convertResult(
        result: PictureResult, context: Context,
        onDone: suspend (Resource<Unit>) -> Unit
    ) {
        result.toBitmap {
            it?.let { bitmap ->
                val t = Bitmap.createScaledBitmap(
                    bitmap,
                    context.resources.displayMetrics.widthPixels,
                    context.resources.displayMetrics.heightPixels,
                    false
                )
                tmpResultBitmap?.recycle()
                tmpResultBitmap = t
                viewModelScope.launch {
                    tmpResultFile = tmpResultBitmap!!.toFile()
                    onDone.invoke(Resource.Success(Unit))
                }
            }
        }
    }

    fun getTempResult() = tmpResultBitmap!!

    fun processWithoutTemplate(callback: sTypeAction<Resource<ScanResult>>) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                dataRepositorySource.processWithoutTemplate(tmpResultFile!!).collect {
                    it.logd()
                    val text = it.map {
                        it?.metadata?.textMetadata?.map { it.text }?.reversed()?.mergeAll()
                    }
                    text.logd()
                    callback.invoke(text.map {
                        ScanResult.SimpleResult(result = it!!)
                    })
                }
            }
        }
    }

    fun processTemplate(id: String, callback: sTypeAction<Resource<ScanResult>>) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                dataRepositorySource.processTemplate(tmpResultFile!!, id).collect {
                    it.logd()
                    it.whenSuccess {
                        downloadFile(it.data!!.fileUrl, ".docx") { path ->
                            viewModelScope.launch {
                                callback.invoke(
                                    Resource.Success(
                                        ScanResult.TemplateResult(
                                            templateId = id,
                                            fileUrl = path
                                        )
                                    )
                                )
                            }

                        }

                    }.whenError {
                        callback.invoke(Resource.Error(message = it.message))
                    }
                }
            }
        }
    }

    fun processTable(context: Context, callback: sTypeAction<Resource<ScanResult>>) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                dataRepositorySource.processTableMetadata(tmpResultFile!!).collect {
                    it.whenSuccess {
                        withContext(Dispatchers.IO) {
                            val metadataTemp =
                                File.createTempFile("metadata", ".json")

                            val writer = PrintWriter(metadataTemp)
                            writer.println(it.data)
                            writer.close()

                            dataRepositorySource.processTable(metadataTemp, "result.xlsx")
                                .collect {
                                    XXPermissions.with(context)
                                        .permission(
                                            android.Manifest.permission.READ_MEDIA_VIDEO,
                                            android.Manifest.permission.READ_MEDIA_IMAGES,
                                            android.Manifest.permission.READ_MEDIA_AUDIO,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        )
                                        .request { _, granted ->
                                            viewModelScope.launch {

                                                it.whenSuccess {
                                                    val savedDir = File(
                                                        Environment.getExternalStoragePublicDirectory(
                                                            Environment.DIRECTORY_DOWNLOADS
                                                        ),
                                                        "OCR"
                                                    ).apply {
                                                        if (!exists()) {
                                                            mkdirs()
                                                        }
                                                    }
                                                    OkDownloaderManager.download(
                                                        listOf(
                                                            File(
                                                                savedDir,
                                                                "result_${System.currentTimeMillis()}.xlsx"
                                                            ).absolutePath
                                                        ),
                                                        listOf(
                                                            it.data!!.fileUrl.replace(
                                                                "http://localhost:3502/",
                                                                baseurl
                                                            )
                                                        ),
                                                        onSuccess = { name, path ->
                                                            path.logd()
                                                            viewModelScope.launch {
                                                                callback.invoke(
                                                                    Resource.Success(
                                                                        data =
                                                                        ScanResult.TableResult(
                                                                            fileUrl = path
                                                                        )
                                                                    )
                                                                )
                                                            }

                                                        }
                                                    )

                                                }.whenError {
                                                    callback.invoke(Resource.Error(message = it.message))
                                                }
                                            }
                                        }
                                }
                        }
                    }.whenError {
                        callback.invoke(Resource.Error(message = it.message))
                    }

                }

            }
        }
    }

    fun processPictureResult(callback: sTypeAction<Resource<ScanResult>>) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                dataRepositorySource.processCCCD(tmpResultFile!!).collect {
                    callback.invoke(it.map { ScanResult.CCCDResult(ocrCCCD = it!!) })
                }
            }
        }
    }

    private fun downloadFile(url: String, extension: String, onFinish: (String) -> Unit) {
        val savedDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ),
            "OCR"
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        OkDownloaderManager.download(
            listOf(File(savedDir, "result_${System.currentTimeMillis()}.$extension").absolutePath),
            listOf(url.replace("http://localhost:3502/", baseurl)),
            onSuccess = { name, path ->
                path.logd()
                viewModelScope.launch {
                    onFinish.invoke(path)
                }

            }
        )

    }

    fun retake() {
        try {
            tmpResultBitmap?.recycle()
            tmpResultBitmap = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun List<String>.mergeAll(): String {
        return StringBuilder().apply {
            this@mergeAll.forEach { append(it + "\n") }
        }.toString()
    }
}
