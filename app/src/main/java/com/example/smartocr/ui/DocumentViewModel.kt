package com.example.smartocr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.util.TypeAction
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {

    suspend fun listCCCD() = dataRepositorySource.listCCCD()

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
}

sealed class DocumentSideEffect {
    class Toast(val message: String) : DocumentSideEffect()

}
