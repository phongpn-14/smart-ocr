package com.example.smartocr.data.remote.service

import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseOcrCCCD
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SmartOCRService {
    @GET("/")
    suspend fun helloWorld(): Response<ResponseHelloWorld>

    @Multipart
    @POST("/api/ocr_cccd")
    suspend fun processOcrCCCD(@Part image: MultipartBody.Part): Response<ResponseOcrCCCD>

    @Multipart
    @POST("/api/template")
    suspend fun processTemplate(
        @Part file: MultipartBody.Part,
        @Part templateId: MultipartBody.Part
    ): Response<ResponseTemplate>

    @Multipart
    @POST("/api/ocr")
    suspend fun processWithoutTemplate(@Part file: MultipartBody.Part): Response<ResponseTemplateMetadata>

    @Multipart
    @POST("/api/ocr")
    suspend fun processTableMetadata(@Part file: MultipartBody.Part): Response<String>

    @Multipart
    @POST("/api/table/save_table")
    suspend fun processTable(
        @Part file: MultipartBody.Part,
        @Part fileName: MultipartBody.Part
    ): Response<ResponseTable>
}