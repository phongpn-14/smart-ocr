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
import com.example.smartocr.util.dp
import com.example.smartocr.util.logd
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
    private var tmpResultBitmap: Bitmap? = null
    lateinit var scanJob: ScanJob

    fun convertResult(result: PictureResult, onDone: suspend (Resource<Unit>) -> Unit) {
        result.toBitmap {
            it?.let { bitmap ->
                val centerX = bitmap.width / 2f
                val centerY = bitmap.height / 2f - 100.dp
                val left = centerX - 150.dp
                val top = centerY - 100.dp
                bitmap.toFile()
                tmpResultBitmap?.recycle()
                if (top <= 0) {
                    viewModelScope.launch {
                        onDone.invoke(Resource.Error(message = "Something wrong. Please try again later."))
                    }
                    return@toBitmap
                }
                "left = $left, top = $top, width = ${bitmap.width},height = ${bitmap.height}".logd()
                tmpResultBitmap =
                    Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), 300.dp, 200.dp)
                viewModelScope.launch {
                    onDone.invoke(Resource.Success(Unit))
                }
            }
        }
    }

    fun getTempResult() = tmpResultBitmap!!

    fun processWithoutTemplate(callback: suspend (Resource<ScanResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val file = tmpResultBitmap!!.toFile()
                dataRepositorySource.processWithoutTemplate(file).collect {
                    it.logd()
                    val text = it.map { it?.metadata?.textMetadata?.map { it.text }?.mergeAll() }
                    text.logd()
                    callback.invoke(text.map {
                        ScanResult.SimpleResult(result = it!!)
                    })
                }
            }
        }
    }

    fun processTemplate(id: String, callback: suspend (Resource<ScanResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val file = tmpResultBitmap!!.toFile()
                dataRepositorySource.processTemplate(file, id).collect {
                    it.logd()
                    callback.invoke(it.map {
                        ScanResult.TemplateResult(
                            templateId = id,
                            fileUrl = it!!.fileUrl
                        )
                    })
                }
            }
        }
    }

    fun processTable(context: Context, callback: suspend (Resource<ScanResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val file = tmpResultBitmap!!.toFile()
                dataRepositorySource.processTableMetadata(file).collect {
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
                                            android.Manifest.permission.READ_MEDIA_IMAGES
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

    fun processPictureResult(callback: suspend (Resource<ScanResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val file = tmpResultBitmap!!.toFile()
                dataRepositorySource.processCCCD(file).collect {
                    callback.invoke(it.map { ScanResult.CCCDResult(ocrCCCD = it!!) })
                }
            }
        }
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
