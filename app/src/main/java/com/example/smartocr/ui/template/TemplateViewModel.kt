package com.example.smartocr.ui.template

import androidx.lifecycle.ViewModel
import com.example.smartocr.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    val templateId = MutableStateFlow(listOf("1", "2", "3"))
}