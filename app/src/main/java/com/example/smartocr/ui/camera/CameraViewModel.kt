package com.example.smartocr.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.util.Action
import com.example.smartocr.util.dp
import com.example.smartocr.util.toFile
import com.otaliastudios.cameraview.PictureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {
    private var tmpResultBitmap: Bitmap? = null

    fun convertResult(result: PictureResult, onDone: Action) {
        result.toBitmap {
            it?.let { bitmap ->
                val centerX = bitmap.width / 2f
                val centerY = bitmap.height / 2f - 100.dp
                val left = centerX - 150.dp
                val top = centerY - 100.dp
                tmpResultBitmap?.recycle()
                "left = $left, top = $top, width = ${bitmap.width},height = ${bitmap.height}"
                tmpResultBitmap = Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), 300.dp, 200.dp)
                onDone.invoke()
            }
        }
    }

    fun getTempResult() = tmpResultBitmap!!

    fun processPictureResult(callback: suspend (Resource<OcrCCCD>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val file = tmpResultBitmap!!.toFile()
                dataRepositorySource.processCCCD(file).collect {
                    callback.invoke(it)
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
}
