package com.example.smartocr.data.remote

import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseOcrCCCD
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.model.OcrCCCD
import kotlinx.coroutines.flow.Flow
import java.io.File

internal interface RemoteDataSource {
    fun helloWorld(): Flow<Resource<ResponseHelloWorld>>
    fun processOcrCCCD(image: File): Flow<Resource<OcrCCCD>>
    fun processWithoutTemplate(file: File): Flow<Resource<ResponseTemplateMetadata>>
    fun processTemplate(file: File, templateId: String): Flow<Resource<ResponseTemplate>>
    fun processTableMetadata(file: File): Flow<Resource<String>>
    fun processTable(file: File, fileName: String):Flow<Resource<ResponseTable>>
}
