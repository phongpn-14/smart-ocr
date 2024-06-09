package com.example.smartocr.data

import com.example.smartocr.data.dto.response.ResponseListScannedCCCDItem
import com.example.smartocr.data.dto.response.ResponseLogin
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.data.model.FileDocument
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.data.model.TemplateKey
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataRepositorySource {
    suspend fun helloWorld(): Flow<Resource<String>>

    suspend fun processCCCD(image: File): Flow<Resource<OcrCCCD>>

    suspend fun processWithoutTemplate(file: File): Flow<Resource<ResponseTemplateMetadata>>

    suspend fun processTemplate(file: File, templateId: String): Flow<Resource<ResponseTemplate>>

    suspend fun processTableMetadata(
        file: File,
    ): Flow<Resource<String>>

    suspend fun processTable(file: File, fileName: String): Flow<Resource<ResponseTable>>

    suspend fun login(username: String, password: String): Flow<Resource<ResponseLogin>>

    suspend fun signIn(
        username: String,
        password: String,
        rePassword: String,
        phone: String
    ): Flow<Resource<String>>

    suspend fun listCCCD(): Flow<Resource<List<OcrCCCD>>>

    suspend fun editCCCD(documentId: String, file: File): Flow<Resource<String>>

    fun deleteCCCD(documentId: String): Flow<Resource<String>>

    suspend fun listTemplate(): Flow<Resource<List<Template>>>

    fun createKeyTemplate(data: String, keyName: String): Flow<Resource<String>>

    fun listKeyTemplate(): Flow<Resource<List<TemplateKey>>>

    fun autoFill(templateId: String, documentId: String): Flow<Resource<ResponseTemplate>>

    fun getAllFileResult(): Flow<Resource<List<FileDocument>>>

    fun deleteFileResult(id: String): Flow<Resource<Unit>>
}

