package com.example.smartocr.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.util.dp
import com.example.smartocr.util.toFile
import com.otaliastudios.cameraview.PictureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val dataRepositorySource: DataRepositorySource
) : ViewModel() {
    fun processPictureResult(result: PictureResult, callback: suspend (Resource<OcrCCCD>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            callback.invoke(Resource.Loading)
            result.toBitmap {
                viewModelScope.launch(Dispatchers.Default) {
                    it?.let { bitmap ->
                        val centerX = bitmap.width / 2f
                        val centerY = bitmap.height / 2f - 100.dp
                        val left = centerX - 150.dp
                        val top = centerY - 100.dp
                        val cutOff =
                            Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), 300.dp, 200.dp)
                        val file = cutOff.toFile()
                        dataRepositorySource.processCCCD(file).collect {
                            callback.invoke(it)
                        }
                    } ?: run {
                        callback.invoke(Resource.Error(message = "Can't read image from file. Cause = cameraView error"))
                    }

                }

            }
        }
    }
}
