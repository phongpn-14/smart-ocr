package com.example.smartocr.ui.document

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.data.remote.baseurl
import com.example.smartocr.util.OkDownloaderManager
import com.example.smartocr.util.TypeAction
import com.example.smartocr.util.event
import com.example.smartocr.util.logd
import com.example.smartocr.util.sTypeAction
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {
    private val retry = MutableStateFlow(Unit.event())
    suspend fun listCCCD() = dataRepositorySource.listCCCD()

    fun deleteCCCD(id: String, callback: sTypeAction<Resource<String>>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepositorySource.deleteCCCD(id).collect {
                callback.invoke(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun listFileResult() = retry.flatMapConcat {
        dataRepositorySource.getAllFileResult()
    }

    fun deleteFileResult(id: String, callback: sTypeAction<Resource<Unit>>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepositorySource.deleteFileResult(id).collect {
                it.whenSuccess {
                    callback.invoke(it)
                    retry.emit(Unit.event())
                }
            }
        }
    }

    fun editCCCD(ocrCCCD: OcrCCCD, onResult: TypeAction<Resource<String>>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepositorySource.editCCCD(
                ocrCCCD.objectID,
                GsonBuilder().apply { setLenient() }.create().toJson(ocrCCCD).toFile()
            )
                .collect {
                    onResult.invoke(it)
                }
        }
    }

    fun String.toFile(): File {
        val file = File.createTempFile("cccd_temp", ".json")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            fos.write(toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos?.close()
        }
        return file
    }


    fun downloadFile(
        context: Context,
        url: String,
        extension: String,
        onFinish: (String) -> Unit
    ) {
        val savedDir = File(context.cacheDir, "OCR").apply {
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

}

sealed class DocumentSideEffect {
    class Toast(val message: String) : DocumentSideEffect()

}
