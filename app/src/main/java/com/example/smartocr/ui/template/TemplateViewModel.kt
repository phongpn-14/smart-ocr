package com.example.smartocr.ui.template

import androidx.lifecycle.ViewModel
import com.example.smartocr.data.DataRepository
import com.example.smartocr.data.dto.response.Template
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    val templateId = MutableStateFlow(listOf("1", "2", "3"))
    private val selectedTemplate = MutableStateFlow<Template?>(null)
    private val retry = MutableStateFlow(Unit)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val listTemplate = retry.flatMapConcat {
        listTemplate()
    }

    val listTemplateState = listTemplate.combine(selectedTemplate) { listResource, selected ->
        listResource.map {
            it!!.map { template ->
                ItemTemplateState(template, template.id == selected?.id)
            }
        }
    }

    suspend fun listTemplate() = dataRepository.listTemplate()
}