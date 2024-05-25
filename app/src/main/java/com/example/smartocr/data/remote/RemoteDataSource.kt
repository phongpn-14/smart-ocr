package com.example.smartocr.data.remote

import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseLogin
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.data.model.OcrCCCD
import kotlinx.coroutines.flow.Flow
import java.io.File

internal interface RemoteDataSource {
    fun helloWorld(): Flow<Resource<ResponseHelloWorld>>
    fun processOcrCCCD(image: File): Flow<Resource<OcrCCCD>>
    fun processWithoutTemplate(file: File): Flow<Resource<ResponseTemplateMetadata>>
    fun processTemplate(file: File, templateId: String): Flow<Resource<ResponseTemplate>>
    fun processTableMetadata(file: File): Flow<Resource<String>>
    fun processTable(file: File, fileName: String): Flow<Resource<ResponseTable>>

    fun login(userName: String, password: String): Flow<Resource<ResponseLogin>>
    fun listCCCD(): Flow<Resource<List<OcrCCCD>>>
    fun editCCCD(documentId: String, file: File): Flow<Resource<String>>

    fun listTemplate(): Flow<Resource<List<Template>>>
}
