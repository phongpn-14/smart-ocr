package com.example.smartocr.data.remote

import com.example.smartocr.data.Resource
import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseListScannedCCCDItem
import com.example.smartocr.data.dto.response.ResponseLogin
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.data.model.Document
import com.example.smartocr.data.model.FileDocument
import com.example.smartocr.data.model.FileDocumentMetadata
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.data.model.TemplateKey
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
    fun signIn(
        userName: String,
        password: String,
        rePassword: String,
        phone: String
    ): Flow<Resource<String>>

    fun listCCCD(): Flow<Resource<List<OcrCCCD>>>
    fun editCCCD(documentId: String, file: File): Flow<Resource<String>>
    fun deleteCCCD(documentId: String): Flow<Resource<String>>

    fun listTemplate(): Flow<Resource<List<Template>>>
    fun createKeyTemplate(data: String, keyName: String): Flow<Resource<String>>
    fun listKeyTemplate(): Flow<Resource<List<TemplateKey>>>
    fun deleteKeyTemplate(id: String): Flow<Resource<String>>
    fun editKeyTemplate(id: String, data: Document): Flow<Resource<String>>

    fun autoFill(templateId: String, documentId: String): Flow<Resource<ResponseTemplate>>
    fun getAllFileResult(): Flow<Resource<List<FileDocument>>>
    fun deleteFileResult(id: String): Flow<Resource<Unit>>
}
