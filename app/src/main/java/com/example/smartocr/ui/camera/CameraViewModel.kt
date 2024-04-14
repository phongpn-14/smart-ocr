package com.example.smartocr.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepositorySource
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.util.logd
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
            val tmp = withContext(Dispatchers.IO) {
                File.createTempFile("_img", ".jpg")
            }
            callback.invoke(Resource.Loading)
            result.toFile(tmp) { resultFile ->
                resultFile!!.logd()
                viewModelScope.launch(Dispatchers.IO) {
                    if (resultFile == null) {
                        callback.invoke(Resource.Error(message = "Can't read image from file. Cause = cameraView error"))
                    } else {
                        dataRepositorySource.processCCCD(resultFile).collect {
                            callback.invoke(it)
                        }
                    }
                }
            }
        }

    }
}
