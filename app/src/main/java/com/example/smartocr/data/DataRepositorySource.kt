package com.example.smartocr.data

import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.model.OcrCCCD
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataRepositorySource {
    suspend fun helloWorld(): Flow<Resource<String>>

    suspend fun processCCCD(image: File): Flow<Resource<OcrCCCD>>

    suspend fun processWithoutTemplate(file: File): Flow<Resource<ResponseTemplateMetadata>>

    suspend fun processTemplate(file: File, templateId: String): Flow<Resource<ResponseTemplate>>
}
