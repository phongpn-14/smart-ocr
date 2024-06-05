package com.example.smartocr.ui.template

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepository
import com.example.smartocr.data.Resource
import com.example.smartocr.data.model.Document
import com.example.smartocr.data.model.TemplateKey
import com.example.smartocr.data.remote.baseurl
import com.example.smartocr.ui.camera.ScanResult
import com.example.smartocr.util.OkDownloaderManager
import com.example.smartocr.util.logd
import com.example.smartocr.util.sTypeAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    private val selectedTemplate = MutableStateFlow<TemplateKey?>(null)
    private val systemTemplate = listOf(
        TemplateKey(Document(name = "Giấy đề nghị thanh toán"), "1"),
        TemplateKey(Document(name = "Giấy thu"), "2"),
        TemplateKey(Document(name = "Phiếu chi"), "3"),
        TemplateKey(Document(name = "Giấy xác nhận sinh viên"), "4")

    )

    private val retry = MutableStateFlow(Unit)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val listTemplate = retry.flatMapConcat {
        listTemplate()
    }

    val listTemplateState = listTemplate.combine(selectedTemplate) { listResource, selected ->
        listResource.map { templateFromApi ->
            (systemTemplate + templateFromApi!!).map { template ->
                ItemTemplateState(template, template.id == selected?.id)
            }
        }
    }

    var currentTemplateId: String = ""

    fun onChooseTemplate(template: TemplateKey) {
        selectedTemplate.value = template
    }

    fun createKey(data: List<String>, name: String, callback: sTypeAction<Resource<String>>) {
        val apiKeyTemplate = StringBuilder()
        data.forEachIndexed { index, s ->
            if (index != 0 && index < data.size) {
                apiKeyTemplate.append(", ")
            }
            apiKeyTemplate.append(s)
        }
        apiKeyTemplate.logd()
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.createKeyTemplate(apiKeyTemplate.toString(), name).collect {
                callback.invoke(it)
                if (it.isSuccess) {
                    retry.emit(Unit)
                }
            }
        }
    }

    fun autoFill(documentId: String, callback: sTypeAction<Resource<ScanResult.TemplateResult>>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.autoFill(currentTemplateId, documentId).collect {
                it.whenSuccess {
                    downloadFile(it.data!!.fileUrl, "docx") { fileUrl ->
                        viewModelScope.launch {
                            callback.invoke(
                                Resource.Success(
                                    data = ScanResult.TemplateResult(
                                        templateId = currentTemplateId,
                                        fileUrl = fileUrl
                                    )
                                )
                            )
                        }

                    }
                }.whenLoading {
                    callback.invoke(it)
                }.whenError { callback.invoke(it) }
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

    suspend fun listTemplate() = dataRepository.listKeyTemplate()
}