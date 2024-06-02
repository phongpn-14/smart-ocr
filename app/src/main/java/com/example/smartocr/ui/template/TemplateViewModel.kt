package com.example.smartocr.ui.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartocr.data.DataRepository
import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.data.model.Document
import com.example.smartocr.data.model.TemplateKey
import com.example.smartocr.util.TypeAction
import com.example.smartocr.util.logd
import com.example.smartocr.util.sTypeAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    private val selectedTemplate = MutableStateFlow<TemplateKey?>(null)
    private val systemTemplate = listOf(
        TemplateKey(Document(name = "Giấy đề nghị thanh toán"), "1"),
        TemplateKey(Document(name = "Giấy thu"), "2"),
        TemplateKey(Document(name = "Phiếu chi"), "3")
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

    fun onChooseTemplate(template: TemplateKey) {
        selectedTemplate.value = template
    }

    fun createKey(data: List<String>, name: String, callback: sTypeAction<Resource<String>>) {
        val apiKeyTemplate = StringBuilder()
        data.forEachIndexed { index, s ->
            if (index != 0 && index < data.size ) {
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

    suspend fun listTemplate() = dataRepository.listKeyTemplate()
}