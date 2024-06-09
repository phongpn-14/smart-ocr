package com.example.smartocr.data.remote.service

import com.example.smartocr.data.dto.response.ResponseHelloWorld
import com.example.smartocr.data.dto.response.ResponseListScannedCCCD
import com.example.smartocr.data.dto.response.ResponseLogin
import com.example.smartocr.data.dto.response.ResponseTable
import com.example.smartocr.data.dto.response.ResponseTemplate
import com.example.smartocr.data.dto.response.ResponseTemplateMetadata
import com.example.smartocr.data.dto.response.Template
import com.example.smartocr.data.model.FileDocument
import com.example.smartocr.data.model.OcrCCCD
import com.example.smartocr.data.model.TemplateKey
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
    suspend fun processOcrCCCD(@Part image: MultipartBody.Part): Response<OcrCCCD>

    @Multipart
    @POST("/api/db/display")
    suspend fun listCCCD(
        @Part dbName: MultipartBody.Part = MultipartBody.Part.createFormData(
            "db_name",
            "db_cccd"
        )
    ): Response<ResponseListScannedCCCD>

    @Multipart
    @POST("/api/db/edit_document")
    suspend fun editCCCD(
        @Part dbName: MultipartBody.Part = MultipartBody.Part.createFormData(
            "db_name",
            "db_cccd"
        ),
        @Part documentId: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Response<String>

    @Multipart
    @POST("/api/db/delete_document")
    suspend fun deleteCCCD(
        @Part dbName: MultipartBody.Part = MultipartBody.Part.createFormData(
            "db_name",
            "db_cccd"
        ),
        @Part documentId: MultipartBody.Part,
    ): Response<String>

    @Multipart
    @POST("/api/ocr_temp")
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

    @Multipart
    @POST("/login")
    suspend fun login(
        @Part userName: MultipartBody.Part,
        @Part password: MultipartBody.Part
    ): Response<ResponseLogin>

    @Multipart
    @POST("/signup")
    suspend fun signIn(
        @Part userName: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part rePassword: MultipartBody.Part,
        @Part phone: MultipartBody.Part,
    ): Response<String>


    @Multipart
    @POST("/api/db/display")
    suspend fun listTemplate(
        @Part dbName: MultipartBody.Part = MultipartBody.Part.createFormData(
            "db_name",
            "db_text"
        )
    ): Response<List<Template>>

    @Multipart
    @POST("/api/db/display")
    suspend fun listTemplateKey(
        @Part dbName: MultipartBody.Part = MultipartBody.Part.createFormData(
            "db_name",
            "db_key"
        )
    ): Response<List<TemplateKey>>

    @Multipart
    @POST("/api/up_key")
    suspend fun createKeyTemplate(
        @Part keys: MultipartBody.Part,
        @Part name: MultipartBody.Part,
    ): Response<String>

    @Multipart
    @POST("/api/db/delete_document")
    suspend fun deleteKeyTemplate(
        @Part dbName: MultipartBody.Part = MultipartBody.Part.createFormData("db_name", "db_key"),
        @Part documentId: MultipartBody.Part,
    ): Response<String>

    @Multipart
    @POST("/api/db/display")
    suspend fun getKeyTemplate(): Response<List<TemplateKey>>

    @Multipart
    @POST("/api/ocr_temp/autofill")
    suspend fun autoFill(
        @Part templateId: MultipartBody.Part,
        @Part documentId: MultipartBody.Part,
    ): Response<ResponseTemplate>

    @Multipart
    @POST("/api/db/display")
    suspend fun getListFileResult(
        @Part name: MultipartBody.Part = MultipartBody.Part.createFormData("db_name", "db_document"),
    ): Response<List<FileDocument>>

    @Multipart
    @POST("/api/db/delete_document")
    suspend fun deleteFileResult(
        @Part name: MultipartBody.Part = MultipartBody.Part.createFormData("db_name", "db_document"),
        @Part documentId : MultipartBody.Part
    ): Response<String>

}